package ape.nexitool.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RotateY180 {
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

  private static void flipTranslation(JsonNode vec) {
    if (vec instanceof ArrayNode v) {
      if (vec.size() >= 3) {
        v.set(0, v.get(0).asDouble() * -1);
        v.set(2, v.get(2).asDouble() * -1);
      }
    }
  }

  private static void flipRotation(JsonNode vec) {
    if (vec instanceof ArrayNode v) {
      if (v.size() == 4) {
        double x = v.get(0).doubleValue();
        double y = v.get(1).doubleValue();
        double z = v.get(2).doubleValue();
        double w = v.get(3).doubleValue();
        v.set(0, -z);
        v.set(1, w);
        v.set(2, -y);
        v.set(3, x);
      }
    }
  }

  private static void scaleNode(JsonNode node) {
    flipTranslation(node.get("translation"));
    flipRotation(node.get("rotation"));
    JsonNode parts = node.get("parts");
    if (parts instanceof ArrayNode pArray) {
      for (int j = 0; j < pArray.size(); j++) {
        JsonNode part = pArray.get(j);
        JsonNode bones = part.get("bones");
        if (bones instanceof ArrayNode bArray) {
          for (int k = 0; k < bArray.size(); k++) {
            JsonNode bone = bArray.get(k);
            flipTranslation(bone.get("translation"));
            flipRotation(bone.get("rotation"));
          }
        }
      }
    }
    JsonNode children = node.get("children");
    if (children instanceof ArrayNode cArray) {
      for (int i = 0; i < cArray.size(); i++) {
        scaleNode(cArray.get(i));
      }
    }
  }

  private static void scaleNodes(JsonNode nodes) {
    if (nodes instanceof ArrayNode array) {
      for (int i = 0; i < array.size(); i++) {
        scaleNode(array.get(i));
      }
    }
  }

  private static void scaleMeshes(JsonNode root) {
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
            offset += count;
            stride += count;
          }
          for (int i = 0; i < verts.size(); i += stride) {
            // (x + z i) * i * i  = (x + z i) * -1 = -x + -z i
            if (posOffset >= 0) {
              double x = verts.get(i + posOffset).doubleValue();
              double z = verts.get(i + posOffset + 2).doubleValue();
              verts.set(i + posOffset, -x);
              verts.set(i + posOffset + 2, -z);
            }
            if (normalOffset >= 0) {
              double x = verts.get(i + normalOffset).doubleValue();
              double z = verts.get(i + normalOffset + 2).doubleValue();
              verts.set(i + normalOffset, -x);
              verts.set(i + normalOffset + 2, -z);
            }
          }
        }
      }
    }
  }

  private static void scaleAnimations(JsonNode root) {
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
                flipTranslation(kf.get("translation"));
                //flipRotation(kf.get("rotation"));
              }
            }
          }
        }
      }
    }
  }

  public static void processPostLoad(JsonNode root, String outputPath) throws IOException {
    scaleMeshes(root);
    scaleNodes(root.get("nodes"));
    scaleAnimations(root);
    Files.writeString(Paths.get(outputPath), root.toPrettyString());
    System.out.println("Done");
  }

  public static void process(String inputPath, String outputPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    processPostLoad(root, outputPath);
  }
}

