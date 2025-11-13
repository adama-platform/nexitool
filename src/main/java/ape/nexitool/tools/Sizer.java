package ape.nexitool.tools;

import ape.nexitool.tools.json.IsStaticMesh;
import ape.nexitool.tools.json.VertexAttributes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Sizer {
  public static void process(String file) throws Exception {
    String json = Files.readString(Paths.get(file));
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode root = (ObjectNode) mapper.readTree(json);
    IsStaticMesh.warnIfNotStaticMesh(root);
    Dimensions dims = new Dimensions(root);
    System.out.println("Static Dimensions:");
    System.out.println(" X: " + dims.min_x + " ->" + dims.max_x);
    System.out.println(" Y: " + dims.min_y + " ->" + dims.max_y);
    System.out.println(" Z: " + dims.min_z + " ->" + dims.max_z);
  }

  public static class Dimensions {
    public double min_x = 1000000;
    public double min_y = 1000000;
    public double min_z = 1000000;
    public double max_x = -1000000;
    public double max_y = -1000000;
    public double max_z = -1000000;
    public int count;

    public Dimensions(JsonNode node) {
      count = 0;
      ArrayNode meshes = (ArrayNode) node.get("meshes");
      for (int k = 0; k < meshes.size(); k++) {
        JsonNode mesh = meshes.get(k);
        VertexAttributes attributes = new VertexAttributes(mesh);
        if (attributes.position >= 0) {
          ArrayNode vertices = (ArrayNode) mesh.get("vertices");
          for (int v = 0; v + attributes.stride - 1 < vertices.size(); v += attributes.stride) {
            double x = vertices.get(v + attributes.position).asDouble();
            double y = vertices.get(v + attributes.position + 1).asDouble();
            double z = vertices.get(v + attributes.position + 2).asDouble();
            min_x = Math.min(min_x, x);
            min_y = Math.min(min_y, y);
            min_z = Math.min(min_z, z);
            max_x = Math.max(max_x, x);
            max_y = Math.max(max_y, y);
            max_z = Math.max(max_z, z);
            count++;
          }
        }
      }
    }

    public Dimensions(JsonNode node, double limit_y) {
      count = 0;
      ArrayNode meshes = (ArrayNode) node.get("meshes");
      for (int k = 0; k < meshes.size(); k++) {
        JsonNode mesh = meshes.get(k);
        VertexAttributes attributes = new VertexAttributes(mesh);
        if (attributes.position >= 0) {
          ArrayNode vertices = (ArrayNode) mesh.get("vertices");
          for (int v = 0; v + attributes.stride - 1 < vertices.size(); v += attributes.stride) {
            double x = vertices.get(v + attributes.position).asDouble();
            double y = vertices.get(v + attributes.position + 1).asDouble();
            double z = vertices.get(v + attributes.position + 2).asDouble();
            if (y < limit_y) {
              min_x = Math.min(min_x, x);
              min_y = Math.min(min_y, y);
              min_z = Math.min(min_z, z);
              max_x = Math.max(max_x, x);
              max_y = Math.max(max_y, y);
              max_z = Math.max(max_z, z);
              count++;
            }
          }
        }
      }
    }
  }
}
