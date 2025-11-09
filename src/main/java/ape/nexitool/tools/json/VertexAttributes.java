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
      String attr = attributes.get(i).asText().toLowerCase().trim();
      switch (attr) {
        case "position":
          _position = at;
          at += 3;
          break;
        case "normal":
          at += 3;
          break;
        case "tangent":
          at += 3;
          break;
        case "binormal":
          at += 3;
          break;
        case "texcoord0":
        case "texcoord1":
        case "texcoord2":
        case "texcoord3":
        case "texcoord4":
          at += 2;
          break;
      }
    }
    this.stride = at;
    this.position = _position;
  }
}
