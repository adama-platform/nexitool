package ape.nexitool.tools;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Sizer {

  public static class VertexAttributes {
    public final int position;
    public final int stride;

    public VertexAttributes(JsonNode mesh) {
      ArrayNode attributes = (ArrayNode) mesh.get("attributes");

      int _position = -1;
      int at = 0;
      for (int i = 0; i < attributes.size(); i++) {
        String attr = attributes.get(i).asText().toLowerCase().trim();
        switch (attr) {
          case "position":
            _position = at;
            at += 3;
            break;
          case "normal":
            at += 3;
            break;
          case "tangent":
            at += 3;
            break;
          case "binormal":
            at += 3;
            break;
          case "texcoord0":
          case "texcoord1":
          case "texcoord2":
          case "texcoord3":
          case "texcoord4":
            at += 2;
            break;
        }
      }
      this.stride = at;
      this.position = _position;
    }
  }

  public static void process(String file) throws Exception {
    String json = Files.readString(Paths.get(file));
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(json);
    ArrayNode meshes = (ArrayNode) node.get("meshes");
    double min_x = 1000000;
    double min_y = 1000000;
    double min_z = 1000000;
    double max_x = -1000000;
    double max_y = -1000000;
    double max_z = -1000000;

    for (int k = 0; k < meshes.size(); k++) {
      JsonNode mesh = meshes.get(k);
      VertexAttributes attributes = new VertexAttributes(mesh);

      if (attributes.position >= 0) {
        ArrayNode vertices = (ArrayNode) mesh.get("vertices");
        for (int v = 0; v + attributes.stride - 1 < vertices.size(); v += attributes.stride) {
          double x = vertices.get(v + attributes.position + 0).asDouble();
          double y = vertices.get(v + attributes.position + 1).asDouble();
          double z = vertices.get(v + attributes.position + 2).asDouble();
          min_x = Math.min(min_x, x);
          min_y = Math.min(min_y, y);
          min_z = Math.min(min_z, z);
          max_x = Math.max(max_x, x);
          max_y = Math.max(max_y, y);
          max_z = Math.max(max_z, z);
        }
      }
    }

    System.out.println("min_x: " + min_x);
    System.out.println("min_y: " + min_y);
    System.out.println("min_z: " + min_z);

    System.out.println("max_x: " + max_x);
    System.out.println("max_y: " + max_y);
    System.out.println("max_z: " + max_z);
  }

  private static void showFields(JsonNode node) {
    node.fieldNames().forEachRemaining(System.out::println);
  }
}
