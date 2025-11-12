package ape.nexitool.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EnsureStatic {

  private static void adjustRootNode(ObjectNode node) {
    if (node.has("translation")) {
      node.remove("translation");
    }
    if (node.has("rotation")) {
      node.remove("rotation");
    }
    JsonNode parts = node.get("parts");
    if (parts instanceof ArrayNode pArray) {
      for (int j = 0; j < pArray.size(); j++) {
        JsonNode part = pArray.get(j);
        if (part.has("bones")) {
          System.out.println("-- CHILDREN NOT ALLOWED IN STATIC OBJECT--");
        }
      }
    }
    if (node.has("children")) {
      System.out.println("-- CHILDREN NOT ALLOWED IN STATIC OBJECT--");
    }

  }
  private static void adjustNodes(JsonNode nodes) {
    if (nodes instanceof ArrayNode array) {
      for (int i = 0; i < array.size(); i++) {
        adjustRootNode((ObjectNode) array.get(i));
      }
    }
  }

  public static void processPostLoad(JsonNode root, String outputPath) throws IOException {
    adjustNodes(root.get("nodes"));
    Files.writeString(Paths.get(outputPath), root.toPrettyString());
    System.out.println("Finished: normalizing object root nodes and doing static test");
  }

  public static void process(String inputPath, String outputPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    processPostLoad(root, outputPath);
  }
}
