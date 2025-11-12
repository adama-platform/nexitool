package ape.nexitool.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Resize {
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

  private static void scaleVector(JsonNode vec, double scale) {
    if (vec instanceof ArrayNode v) {
      for (int i = 0; i < v.size(); i++) {
        double c = v.get(i).doubleValue() * scale;
        v.set(i, c);
      }
    }
  }

  private static void scaleNode(JsonNode node, double scale) {
    scaleVector(node.get("translation"), scale);
    if (node.has("scale")) {
      ArrayNode scaleArray = (ArrayNode) node.get("scale");
      for (int i = 0; i < scaleArray.size(); i++) {
        scaleArray.set(i, 1.0);
      }
    }
    JsonNode parts = node.get("parts");
    if (parts instanceof ArrayNode pArray) {
      for (int j = 0; j < pArray.size(); j++) {
        JsonNode part = pArray.get(j);
        JsonNode bones = part.get("bones");
        if (bones instanceof ArrayNode bArray) {
          for (int k = 0; k < bArray.size(); k++) {
            JsonNode bone = bArray.get(k);
            scaleVector(bone.get("translation"), scale);
          }
        }
      }
    }
    JsonNode children = node.get("children");
    if (children instanceof ArrayNode cArray) {
      for (int i = 0; i < cArray.size(); i++) {
        scaleNode(cArray.get(i), scale);
      }
    }
  }

  private static void scaleNodes(JsonNode nodes, double scale) {
    if (nodes instanceof ArrayNode array) {
      for (int i = 0; i < array.size(); i++) {
        scaleNode(array.get(i), scale);
      }
    }
  }

  private static void scaleMeshes(JsonNode root, double scale) {
    JsonNode meshesNode = root.get("meshes");
    if (meshesNode instanceof ArrayNode meshes) {
      for (int m = 0; m < meshes.size(); m++) {
        JsonNode mesh = meshes.get(m);
        JsonNode attrsNode = mesh.get("attributes");
        JsonNode vertsNode = mesh.get("vertices");
        if (attrsNode instanceof ArrayNode attrs && vertsNode instanceof ArrayNode verts) {
          int stride = 0;
          int posOffset = -1;
          int offset = 0;
          for (int i = 0; i < attrs.size(); i++) {
            String attr = attrs.get(i).asText();
            int count = getComponentCount(attr);
            if ("POSITION".equals(attr)) {
              posOffset = offset;
            }
            offset += count;
            stride += count;
          }
          if (posOffset >= 0) {
            for (int i = 0; i < verts.size(); i += stride) {
              double x = verts.get(i + posOffset).doubleValue() * scale;
              double y = verts.get(i + posOffset + 1).doubleValue() * scale;
              double z = verts.get(i + posOffset + 2).doubleValue() * scale;
              verts.set(i + posOffset, x);
              verts.set(i + posOffset + 1, y);
              verts.set(i + posOffset + 2, z);
            }
          }
        }
      }
    }
  }

  private static void scaleAnimations(JsonNode root, double scale) {
    JsonNode animsNode = root.get("animations");
    if (animsNode instanceof ArrayNode anims) {
      for (int a = 0; a < anims.size(); a++) {
        JsonNode anim = anims.get(a);
        JsonNode boneAnims = anim.get("bones");
        if (boneAnims instanceof ArrayNode baArray) {
          for (int b = 0; b < baArray.size(); b++) {
            JsonNode ba = baArray.get(b);
            JsonNode kfs = ba.get("keyframes");
            if (kfs instanceof ArrayNode kfArray) {
              for (int k = 0; k < kfArray.size(); k++) {
                JsonNode kf = kfArray.get(k);
                scaleVector(kf.get("translation"), scale);
              }
            }
          }
        }
      }
    }
  }

  public static void processPostLoad(JsonNode root, String outputPath, double scale) throws IOException {
    scaleMeshes(root, scale);
    scaleNodes(root.get("nodes"), scale);
    scaleAnimations(root, scale);
    Files.writeString(Paths.get(outputPath), root.toPrettyString());
    System.out.println("Finished: resizing by " + scale);
  }

  public static void process(String inputPath, String outputPath, double scale) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    processPostLoad(root, outputPath, scale);
  }
}

