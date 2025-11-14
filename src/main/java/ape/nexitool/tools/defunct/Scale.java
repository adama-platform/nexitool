package ape.nexitool.tools.defunct;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Scale {

  public static void processPostLoad(JsonNode root, String outputPath, double scale) throws IOException {
    new ape.nexitool.transforms.Scale((float) scale).execute((ObjectNode) root);
    Files.writeString(Paths.get(outputPath), root.toPrettyString());
    System.out.println("Finished: resizing by " + scale);
  }

  public static void process(String inputPath, String outputPath, double scale) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    processPostLoad(root, outputPath, scale);
  }
}

