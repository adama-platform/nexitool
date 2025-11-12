package ape.nexitool.tools.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class VertexAttributes {
  public final int position;
  public final int stride;

  public VertexAttributes(JsonNode mesh) {
    ArrayNode attributes = (ArrayNode) mesh.get("attributes");
    int _position = -1;
    int at = 0;
    for (int i = 0; i < attributes.size(); i++) {
      String attr = attributes.get(i).asText().toUpperCase().toUpperCase();
      int count = getComponentCount(attr);
      if ("POSITION".equals(attr)) {
        _position = at;
      }
      at += count;
    }
    this.stride = at;
    this.position = _position;
  }

  public static int getComponentCount(String attr) {
    if (attr.equals("POSITION") || attr.equals("NORMAL") || attr.equals("TANGENT") || attr.equals("BINORMAL")) {
      return 3;
    } else if (attr.startsWith("TEXCOORD")) {
      return 2;
    } else if (attr.startsWith("COLOR")) {
      return 4;
    } else if (attr.equals("COLORPACKED")) {
      return 1;
    } else if (attr.startsWith("BLENDWEIGHT") || attr.startsWith("BLENDINDICES")) {
      return 2; // WTF
    } else {
      throw new IllegalArgumentException("Unknown attribute: " + attr);
    }
  }
}
