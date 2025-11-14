package ape.nexitool.tools;

import ape.nexitool.tools.json.ParsedVertexAttributes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AlignWithFloor {
  public static void removeTranslation(JsonNode root) {
    ArrayNode nodes = (ArrayNode) root.get("nodes");
    for (int k = 0; k < nodes.size(); k++) {
      ObjectNode node = (ObjectNode) nodes.get(k);
      if (node.has("translation")) { // it is ignored, so we simply remove it
        node.remove("translation");
      }
    }
  }


  private static double getMinY(JsonNode root) {
    double minY = Double.MIN_VALUE;
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
            int count = ParsedVertexAttributes.getComponentCount(attr);
            if ("POSITION".equals(attr)) {
              posOffset = offset;
            }
            offset += count;
            stride += count;
          }
          if (posOffset >= 0) {
            for (int i = 0; i < verts.size(); i += stride) {
              minY = Math.min(minY, verts.get(i + posOffset + 1).asDouble());
            }
          }
        }
      }
    }
    return minY;
  }

  private static double addY(JsonNode root, double y) {
    double minY = Double.MIN_VALUE;
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
            int count = ParsedVertexAttributes.getComponentCount(attr);
            if ("POSITION".equals(attr)) {
              posOffset = offset;
            }
            offset += count;
            stride += count;
          }
          if (posOffset >= 0) {
            for (int i = 0; i < verts.size(); i += stride) {
              verts.set(i + posOffset + 1, verts.get(i + posOffset + 1).asDouble() + y);
            }
          }
        }
      }
    }
    return minY;
  }

  public static void process(String inputPath, String outputPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    removeTranslation(root);
    double minY = getMinY(root);
    addY(root, -minY);


    Files.writeString(Paths.get(outputPath), root.toPrettyString());
    System.out.println("Finished: set to floor of y = 0 (from " + minY + ")");
  }
}
