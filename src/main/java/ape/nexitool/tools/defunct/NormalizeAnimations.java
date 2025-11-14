package ape.nexitool.tools.defunct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Paths;

public class NormalizeAnimations {
  public static void process(String input, String output) throws Exception {
    String json = Files.readString(Paths.get(input));
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode root = (ObjectNode) mapper.readTree(json);
    ArrayNode animations = (ArrayNode) root.get("animations");
    if (animations != null) {
      ArrayNode next = mapper.createArrayNode();
      for (int k = 0; k < animations.size(); k++) {
        ObjectNode animation = (ObjectNode) animations.get(k);
        String id = animation.get("id").asText();
        id = id.trim().toLowerCase();
        animation.put("id", id);
        next.add(animation);
      }
      root.set("animations", next);
    }
    Files.writeString(Paths.get(output), root.toPrettyString());
    System.out.println("Finished: normalizing animation names");
  }
}
