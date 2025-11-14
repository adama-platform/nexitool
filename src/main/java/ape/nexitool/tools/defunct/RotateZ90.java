package ape.nexitool.tools.defunct;

import ape.nexitool.transforms.Rotate;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RotateZ90 {
  private static int getComponentCount(String attr) {
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

  private static void rotateZ90(ArrayNode v, int offset) {
    double x = v.get(offset + 0).asDouble();
    double y = v.get(offset + 1).asDouble();
    double z = v.get(offset + 2).asDouble();
    v.set(offset + 0, -y);
    v.set(offset + 1, x);
    // v.set(offset + 2, z);
  }

  private static void flipMeshes(JsonNode root) {
    JsonNode meshesNode = root.get("meshes");
    if (meshesNode instanceof ArrayNode meshes) {
      for (int m = 0; m < meshes.size(); m++) {
        JsonNode mesh = meshes.get(m);
        JsonNode attrsNode = mesh.get("attributes");
        JsonNode vertsNode = mesh.get("vertices");
        if (attrsNode instanceof ArrayNode attrs && vertsNode instanceof ArrayNode verts) {
          int stride = 0;
          int posOffset = -1;
          int normalOffset = -1;
          int tangentOffset = -1;
          int binormalOffset = -1;
          int offset = 0;
          for (int i = 0; i < attrs.size(); i++) {
            String attr = attrs.get(i).asText();
            int count = getComponentCount(attr);
            if ("POSITION".equals(attr)) {
              posOffset = offset;
            }
            if ("NORMAL".equals(attr)) {
              normalOffset = offset;
            }
            if ("TANGENT".equals(attr)) {
              tangentOffset = offset;
            }
            if ("BINORMAL".equals(attr)) {
              binormalOffset = offset;
            }
            offset += count;
            stride += count;
          }
          for (int i = 0; i < verts.size(); i += stride) {
            if (posOffset >= 0) {
              rotateZ90(verts, i + posOffset);
            }
            if (normalOffset >= 0) {
              rotateZ90(verts, i + normalOffset);
            }
            if (tangentOffset >= 0) {
              rotateZ90(verts, i + tangentOffset);
            }
            if (binormalOffset >= 0) {
              rotateZ90(verts, i + binormalOffset);
            }
          }
        }
      }
    }
  }


  public static void processPostLoad(JsonNode root, String outputPath) throws IOException {
    // flipMeshes(root);
    Matrix3 mat = new Matrix3();
    mat.setToRotation(new Vector3(0, 0, 1), 90);
    new Rotate(mat).execute((ObjectNode) root);
    Files.writeString(Paths.get(outputPath), root.toPrettyString());
    System.out.println("Finished: rotating 90 degrees around z-axis");
  }

  public static void process(String inputPath, String outputPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    processPostLoad(root, outputPath);
  }
}

