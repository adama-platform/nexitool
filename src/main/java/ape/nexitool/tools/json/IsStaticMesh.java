package ape.nexitool.tools.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class IsStaticMesh {
  public static boolean warnIfNotStaticMesh(ObjectNode root) {
    JsonNode nodes = root.get("nodes");
    if (nodes instanceof ArrayNode array && array.size() > 0) {
      for (JsonNode nodeChild : array) {
        ObjectNode node = (ObjectNode) nodeChild;
        if (node.has("translation")) {
          ArrayNode translation = (ArrayNode) node.get("translation");
          for (int k = 0; k < translation.size(); k++) {
            double v = translation.get(k).asDouble();
            if (Math.abs(v) > 0.001) {
              System.out.println("[WARNING: MESH IS NOT STATIC]");
              return false;
            }
          }
        }
        if (node.has("children")) {
          System.out.println("[WARNING: MESH IS NOT STATIC]");
        }
      }
    }
    return true;
  }
}
