package ape.nexitool.tools;

import ape.nexitool.tools.json.VertexAttributes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;

public class Center {

  private static class Vertex {
    public final double x;
    public final double y;
    public final double z;
    public Vertex(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
  }

  private static class Box {
    public final String id;
    public double min_x, min_y, min_z;
    public double max_x, max_y, max_z;
    private boolean shifted;

    public Box(String id) {
      this.id = id;
      this.min_x = Double.POSITIVE_INFINITY;
      this.min_y = Double.POSITIVE_INFINITY;
      this.min_z = Double.POSITIVE_INFINITY;

      this.max_x = Double.NEGATIVE_INFINITY;
      this.max_y = Double.NEGATIVE_INFINITY;
      this.max_z = Double.NEGATIVE_INFINITY;
      this.shifted = false;
    }

    public void add(Vertex v) {
      min_x = Math.min(min_x, v.x);
      min_y = Math.min(min_y, v.y);
      min_z = Math.min(min_z, v.z);
      max_x = Math.max(max_x, v.x);
      max_y = Math.max(max_y, v.y);
      max_z = Math.max(max_z, v.z);
    }

    public void shift(double x, double y, double z) {
      this.min_x += x;
      this.min_y += y;
      this.min_z += z;
      this.max_x += x;
      this.max_y += y;
      this.max_z += z;
      if (this.shifted) {
        System.out.println("[WARNING: MESH HAS BOX ALREADY SHIFTED:" + id + "]");
      }
      this.shifted = true;
    }

    public Box(Box a, Box b) {
      this.id = a.id + "+" + b.id;
      this.min_x = Math.min(a.min_x, b.min_x);
      this.min_y = Math.min(a.min_y, b.min_y);
      this.min_z = Math.min(a.min_z, b.min_z);
      this.max_x = Math.max(a.max_x, b.max_x);
      this.max_y = Math.max(a.max_y, b.max_y);
      this.max_z = Math.max(a.max_z, b.max_z);
      this.shifted = a.shifted || b.shifted;
    }

    @Override
    public String toString() {
      return "Box{" +
              "id='" + id + '\'' +
              ", min_x=" + min_x +
              ", min_y=" + min_y +
              ", min_z=" + min_z +
              ", max_x=" + max_x +
              ", max_y=" + max_y +
              ", max_z=" + max_z +
              ", shifted=" + shifted +
              '}';
    }
  }

  public static TreeMap<Integer, Vertex> extractVertices(JsonNode mesh) {
    TreeMap<Integer, Vertex> map = new TreeMap<Integer, Vertex>();
    int id = 0;
    VertexAttributes attributes = new VertexAttributes(mesh);
    if (attributes.position >= 0) {
      ArrayNode vertices = (ArrayNode) mesh.get("vertices");
      for (int v = 0; v + attributes.stride - 1 < vertices.size(); v += attributes.stride) {
        double x = vertices.get(v + attributes.position + 0).asDouble();
        double y = vertices.get(v + attributes.position + 1).asDouble();
        double z = vertices.get(v + attributes.position + 2).asDouble();
        map.put(id, new Vertex(x, y, z));
        id++;
      }
    }
    return map;
  }

  public static TreeMap<String, Box> extractParts(JsonNode node) {
    TreeMap<String, Box> map = new TreeMap<String, Box>();
    ArrayNode meshes = (ArrayNode) node.get("meshes");
    for (int k = 0; k < meshes.size(); k++) {
      JsonNode mesh = meshes.get(k);
      TreeMap<Integer, Vertex> vertices = extractVertices(mesh);
      ArrayNode parts = (ArrayNode) mesh.get("parts");
      for (int p = 0; p < parts.size(); p++) {
        ObjectNode part = (ObjectNode) parts.get(p);
        String id = part.get("id").asText();
        Box box = new Box(id);
        ArrayNode indices = (ArrayNode) part.get("indices");
        for (int i = 0; i < indices.size(); i++) {
          Vertex v = vertices.get(indices.get(i).asInt());
          if (v != null) {
            box.add(v);
          }
        }
        map.put(id, box);
      }
    }
    return map;
  }

  public static void shiftBoxes(JsonNode root, TreeMap<String, Box> boxes) {
    ArrayNode nodes = (ArrayNode) root.get("nodes");
    for (int k = 0; k < nodes.size(); k++) {
      JsonNode node = nodes.get(k);
      ArrayNode translation = (ArrayNode) node.get("translation");
      double x = 0;
      double y = 0;
      double z = 0;
      if (translation != null) {
        x = translation.get(0).asDouble();
        y = translation.get(1).asDouble();
        z = translation.get(2).asDouble();
      }
      ArrayNode parts = (ArrayNode) node.get("parts");
      if (parts != null) {
        for (int p = 0; p < parts.size(); p++) {
          ObjectNode part = (ObjectNode) parts.get(p);
          if (part.has("meshpartid")) {
            String lookup = part.get("meshpartid").asText();
            Box box = boxes.get(lookup);
            box.shift(x, y, z);
          }
        }
      }
    }
  }

  public static void translateNodes(JsonNode root, double cx, double cy, double cz) {
    ArrayNode nodes = (ArrayNode) root.get("nodes");
    for (int k = 0; k < nodes.size(); k++) {
      JsonNode node = nodes.get(k);
      ArrayNode translation = (ArrayNode) node.get("translation");
      if (translation != null) {
        translation.set(0, translation.get(0).asDouble() - cx);
        translation.set(1, translation.get(1).asDouble() - cy);
        translation.set(2, translation.get(2).asDouble() - cz);
      }
    }
  }


  public static void process(String inputPath, String outputPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    TreeMap<String, Box> boxes = extractParts(root);
    shiftBoxes(root, boxes);
    Box box = null;
    for (Box b : boxes.values()) {
      if (box == null) {
        box = b;
      } else {
        box = new Box(box, b);
      }
    }

    double cx = (box.min_x + box.max_x) / 2.0;
    double cy = (box.min_y + box.max_y) / 2.0;
    double cz = (box.min_z + box.max_z) / 2.0;
    translateNodes(root, cx, cy, cz);
    System.out.println("CX: " + Math.round(cx * 10) / 10 + " CY: " + Math.round(cy * 10) / 10 + " CZ: " + Math.round(cz * 10) / 10);

    Files.writeString(Paths.get(outputPath), root.toPrettyString());
  }
}
