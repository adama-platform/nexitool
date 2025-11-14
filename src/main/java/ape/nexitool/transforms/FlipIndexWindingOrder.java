package ape.nexitool.transforms;

import ape.nexitool.contracts.Transform;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FlipIndexWindingOrder implements Transform {
  @Override
  public void execute(ObjectNode root) {
    ArrayNode meshes = (ArrayNode) root.get("meshes");
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
}
