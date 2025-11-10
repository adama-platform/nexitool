package ape.nexitool.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CopyAnimations {
  public static void process(String animations, String model, String outputPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootAnimations = mapper.readTree(new File(animations));
    ObjectNode rootModel = (ObjectNode) mapper.readTree(new File(model));
    rootModel.set("animations", rootAnimations.get("animations"));
    Files.writeString(Paths.get(outputPath), rootModel.toPrettyString());
    System.out.println("Done");
  }
}
