package ape.nexitool.transforms;

import ape.nexitool.contracts.Transform;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Normalize implements Transform {
  @Override
  public void execute(ObjectNode root) {
    ArrayNode materials = (ArrayNode) root.get("materials");
    TreeMap<String, String> materialMap = new TreeMap<>();
    String[] next = new String[]{"a", "b", "c", "d", "e", "f"};
    for (int k = 0; k < materials.size(); k++) {
      ObjectNode material = (ObjectNode) materials.get(k);
      Iterator<Map.Entry<String, JsonNode>> fIt = material.fields();
      while (fIt.hasNext()) {
        Map.Entry<String, JsonNode> f = fIt.next();
        if (!f.getKey().equals("id")) {
          fIt.remove();
        }
      }
      materialMap.put(material.get("id").asText(), next[k]);
      System.out.println("Progress: mapping '" + material.get("id").asText() + "' to " + next[k]);
      material.put("id", next[k]);
    }
    if (root.has("nodes")) {
      ArrayNode nodes = (ArrayNode) root.get("nodes");
      for (int k = 0; k < nodes.size(); k++) {
        ObjectNode node = (ObjectNode) nodes.get(k);
        if (node.has("parts")) {
          ArrayNode parts = (ArrayNode) node.get("parts");
          for (int k2 = 0; k2 < parts.size(); k2++) {
            ObjectNode part = (ObjectNode) parts.get(k2);
            if (part.has("materialid")) {
              part.put("materialid", materialMap.get(part.get("materialid").asText()));
            }
          }
        }
      }
    }
    ArrayNode animations = (ArrayNode) root.get("animations");
    if (animations != null) {
      for (int k = 0; k < animations.size(); k++) {
        ObjectNode animation = (ObjectNode) animations.get(k);
        String id = animation.get("id").asText();
        id = id.trim().toLowerCase();
        animation.put("id", id);
      }
    }
  }
}
