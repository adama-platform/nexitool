package ape.nexitool.tools;

import ape.nexitool.tools.json.VertexAttributes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Sizer {
  public static void process(String file) throws Exception {
    String json = Files.readString(Paths.get(file));
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(json);
    Dimensions dims = new Dimensions(node);
    System.out.println("min_x: " + dims.min_x);
    System.out.println("min_y: " + dims.min_y);
    System.out.println("min_z: " + dims.min_z);
    System.out.println("max_x: " + dims.max_x);
    System.out.println("max_y: " + dims.max_y);
    System.out.println("max_z: " + dims.max_z);
  }

  public static class Dimensions {
    public double min_x = 1000000;
    public double min_y = 1000000;
    public double min_z = 1000000;
    public double max_x = -1000000;
    public double max_y = -1000000;
    public double max_z = -1000000;

    public Dimensions(JsonNode node) {
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
          }
        }
      }
    }
  }
}
