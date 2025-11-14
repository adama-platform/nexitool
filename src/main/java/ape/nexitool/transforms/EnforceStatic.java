package ape.nexitool.transforms;

import ape.nexitool.contracts.Transform;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EnforceStatic implements Transform {
  @Override
  public void execute(ObjectNode root) {
    if (root.get("nodes") instanceof ArrayNode array) {
      for (int i = 0; i < array.size(); i++) {
        enforceStatic((ObjectNode) array.get(i));
      }
    }
    if (root.has("animations")) {
      if (root.get("animations") instanceof ArrayNode array) {
        if (array.size() > 0) {
          System.out.println("[ANIMATIONS NOT ALLOWED IN STATIC OBJECT : REMOVING]");
          array.removeAll();
        }
      }
    }
  }


  private void enforceStatic(ObjectNode node) {
    if (node.has("translation")) {
      node.remove("translation");
    }
    if (node.has("rotation")) {
      node.remove("rotation");
    }
    JsonNode parts = node.get("parts");
    if (parts instanceof ArrayNode pArray) {
      for (int j = 0; j < pArray.size(); j++) {
        ObjectNode part = (ObjectNode) pArray.get(j);
        if (part.has("bones")) {
          part.remove("bones");
          System.out.println("[BONES NOT ALLOWED IN STATIC OBJECT : REMOVING]");
        }
      }
    }
    if (node.has("children")) {
      System.out.println("[CHILDREN NOT ALLOWED IN STATIC OBJECT : REMOVING]");
      node.remove("children");
    }
  }
}
