package ape.nexitool.transforms.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestStatic {
  public static boolean is(ObjectNode root) {
    if (root.get("nodes") instanceof ArrayNode array) {
      for (int i = 0; i < array.size(); i++) {
        if (!testNode((ObjectNode) array.get(i))) {
          return false;
        }
      }
    }
    if (root.has("animations")) {
      if (root.get("animations") instanceof ArrayNode array) {
        return array.size() <= 0;
      }
    }
    return true;
  }

  private static boolean testNode(ObjectNode node) {
    JsonNode parts = node.get("parts");
    if (parts instanceof ArrayNode pArray) {
      for (int j = 0; j < pArray.size(); j++) {
        ObjectNode part = (ObjectNode) pArray.get(j);
        if (part.has("bones")) {
          return false;
        }
      }
    }
    return !node.has("children");
  }
}
