package ape.nexitool.tools.defunct;

import ape.nexitool.transforms.FlipIndexWindingOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FlipIndices {

  public static void flipIndices(JsonNode node) {
    ArrayNode meshes = (ArrayNode) node.get("meshes");
    for (int k = 0; k < meshes.size(); k++) {
      JsonNode mesh = meshes.get(k);
      ArrayNode parts = (ArrayNode) mesh.get("parts");
      for (int p = 0; p < parts.size(); p++) {
        ObjectNode part = (ObjectNode) parts.get(p);
        ArrayNode indices = (ArrayNode) part.get("indices");
        for (int i = 0; i + 2 < indices.size(); i += 3) {
          // leave 0 alone, swap +1 and +2
          int a = indices.get(i + 1).asInt();
          int b = indices.get(i + 2).asInt();
          indices.set(i + 1, b);
          indices.set(i + 2, a);
        }
      }
    }
  }

  public static void process(String inputPath, String outputPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    new FlipIndexWindingOrder().execute((ObjectNode) root);
    //flipIndices(root);
    Files.writeString(Paths.get(outputPath), root.toPrettyString());
    System.out.println("Finished: flipping indices");
  }
}
