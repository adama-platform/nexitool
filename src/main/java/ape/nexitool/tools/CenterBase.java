package ape.nexitool.tools;

import ape.nexitool.tools.json.IsStaticMesh;
import ape.nexitool.tools.json.VertexAttributes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CenterBase {
  public static void processPostLoad(JsonNode root, String outputPath, double max_y) throws IOException {
    Sizer.Dimensions dimensions = new Sizer.Dimensions(root, max_y);
    System.out.println("[center-base] base contains:" + dimensions.count + " vertices");
    if (dimensions.count > 0) {
      double cx = (dimensions.max_x + dimensions.min_x) / 2.0;
      double cz = (dimensions.max_z + dimensions.min_z) / 2.0;
      offsetMeshes(root, cx, cz);
      System.out.println("Finished: centered to " + cx + "," + cz);
    } else {
      System.out.println("Finished: no vertices near base (align floor?)");
    }
    Files.writeString(Paths.get(outputPath), root.toPrettyString());
  }


  private static void offsetMeshes(JsonNode root, double dx, double dz) {
    JsonNode meshesNode = root.get("meshes");
    if (meshesNode instanceof ArrayNode meshes) {
      for (int m = 0; m < meshes.size(); m++) {
        JsonNode mesh = meshes.get(m);
        JsonNode attrsNode = mesh.get("attributes");
        JsonNode vertsNode = mesh.get("vertices");
        if (attrsNode instanceof ArrayNode attrs && vertsNode instanceof ArrayNode verts) {
          int stride = 0;
          int posOffset = -1;
          int offset = 0;
          for (int i = 0; i < attrs.size(); i++) {
            String attr = attrs.get(i).asText();
            int count = VertexAttributes.getComponentCount(attr);
            if ("POSITION".equals(attr)) {
              posOffset = offset;
            }
            offset += count;
            stride += count;
          }
          if (posOffset >= 0) {
            for (int i = 0; i < verts.size(); i += stride) {
              double x = verts.get(i + posOffset).doubleValue() - dx;
              double z = verts.get(i + posOffset + 2).doubleValue() - dz;
              verts.set(i + posOffset, x);
              verts.set(i + posOffset + 2, z);
            }
          }
        }
      }
    }
  }


  public static void process(String inputPath, String outputPath, double max_y) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode root = (ObjectNode) mapper.readTree(new File(inputPath));
    IsStaticMesh.warnIfNotStaticMesh(root);
    processPostLoad(root, outputPath, max_y);
  }
}
