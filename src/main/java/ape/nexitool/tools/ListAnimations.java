package ape.nexitool.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ListAnimations {
  public static void process(String model) throws Exception {
    String json = Files.readString(Paths.get(model));
    ObjectMapper mapper = new ObjectMapper();
    ArrayNode root = (ArrayNode) mapper.readTree(json).get("animations");
    for (int k = 0; k < root.size(); k++) {
      JsonNode animation = root.get(k);
      System.out.println(animation.get("id").asText());
    }
  }
}
