package ape.nexitool.tools.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ParsedVertexAttributes {
  public final int position;
  public final int normal;
  public final int tangent;
  public final int binormal;
  public final int stride;

  public ParsedVertexAttributes(JsonNode mesh) {
    ArrayNode attributes = (ArrayNode) mesh.get("attributes");
    int _position = -1;
    int _normal = -1;
    int _tangent = -1;
    int _binormal = -1;
    int at = 0;
    for (int i = 0; i < attributes.size(); i++) {
      String attr = attributes.get(i).asText().toUpperCase().toUpperCase();
      int count = getComponentCount(attr);
      if ("POSITION".equals(attr)) {
        _position = at;
      }
      if ("NORMAL".equals(attr)) {
        _normal = at;
      }
      if ("TANGENT".equals(attr)) {
        _tangent = at;
      }
      if ("BINORMAL".equals(attr)) {
        _binormal = at;
      }
      at += count;
    }
    this.stride = at;
    this.position = _position;
    this.normal = _normal;
    this.tangent = _tangent;
    this.binormal = _binormal;
  }

  public static ParsedVertexAttributes fromMesh(JsonNode mesh) {
    JsonNode attrsNode = mesh.get("attributes");
    if (attrsNode instanceof ArrayNode) {
      return new ParsedVertexAttributes(mesh);
    }
    return null;
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
