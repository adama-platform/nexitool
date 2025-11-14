package ape.nexitool.tools.defunct;

import ape.nexitool.transforms.EnforceStatic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EnsureStatic {

  public static void processPostLoad(JsonNode root, String outputPath) throws IOException {
    new EnforceStatic().execute((ObjectNode) root);
    Files.writeString(Paths.get(outputPath), root.toPrettyString());
    System.out.println("Finished: normalizing object root nodes and doing static test");
  }

  public static void process(String inputPath, String outputPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    processPostLoad(root, outputPath);
  }
}
