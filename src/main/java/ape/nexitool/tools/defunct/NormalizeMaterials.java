package ape.nexitool.tools.defunct;

import ape.nexitool.transforms.Normalize;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Paths;

public class NormalizeMaterials {
  public static void process(String input, String output) throws Exception {
    String json = Files.readString(Paths.get(input));
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(json);
    new Normalize().execute((ObjectNode) root);
    // final:
    Files.writeString(Paths.get(output), root.toPrettyString());
    System.out.println("Finished: normalizing materials to nexidrive registers");
  }
}
