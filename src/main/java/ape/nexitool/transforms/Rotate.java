package ape.nexitool.transforms;

import ape.nexitool.contracts.Transform;
import ape.nexitool.transforms.support.ParsedVertexAttributes;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.TreeSet;

public class Rotate implements Transform {
  private final Matrix3 rotation;

  public Rotate(Matrix3 rotation) {
    this.rotation = rotation;
  }

  @Override
  public void execute(ObjectNode root) {
    // do meshes
    if (root.get("meshes") instanceof ArrayNode meshes) {
      for (int m = 0; m < meshes.size(); m++) {
        JsonNode mesh = meshes.get(m);
        JsonNode vertsNode = mesh.get("vertices");
        ParsedVertexAttributes attributes = ParsedVertexAttributes.fromMesh(mesh);
        if (vertsNode instanceof ArrayNode verts && attributes != null) {
          for (int i = 0; i < verts.size(); i += attributes.stride) {
            if (attributes.position >= 0) {
              apply(verts, i + attributes.position);
            }
            if (attributes.normal >= 0) {
              apply(verts, i + attributes.normal);
            }
            if (attributes.tangent >= 0) {
              apply(verts, i + attributes.tangent);
            }
            if (attributes.binormal >= 0) {
              apply(verts, i + attributes.binormal);
            }
          }
        }
      }
    }
    // do nodes
    TreeSet<String> roots = new TreeSet<>();
    if (root.get("nodes") instanceof ArrayNode array) {
      for (int i = 0; i < array.size(); i++) {
        applyNode(array.get(i), roots);
      }
    }
    // rotate animations
    if (root.get("animations") instanceof ArrayNode anims) {
      for (int a = 0; a < anims.size(); a++) {
        JsonNode anim = anims.get(a);
        JsonNode boneAnims = anim.get("bones");
        if (boneAnims instanceof ArrayNode baArray) {
          for (int b = 0; b < baArray.size(); b++) {
            JsonNode ba = baArray.get(b);
            if (ba.has("bone") && roots.contains(ba.get("bone").asText())) {
              JsonNode kfs = ba.get("keyframes");
              if (kfs instanceof ArrayNode kfArray) {
                for (int k = 0; k < kfArray.size(); k++) {
                  JsonNode kf = kfArray.get(k);
                  if (kf.get("translation") instanceof ArrayNode translation) {
                    apply(translation, 0);
                  }
                  if (kf.get("rotation") instanceof ArrayNode rotation) {
                    applyQ(rotation);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void apply(ArrayNode v, int offset) {
    Vector3 vec = new Vector3();
    vec.x = (float) v.get(offset).asDouble();
    vec.y = (float) v.get(offset + 1).asDouble();
    vec.z = (float) v.get(offset + 2).asDouble();
    vec.mul(rotation);
    v.set(offset, vec.x);
    v.set(offset + 1, vec.y);
    v.set(offset + 2, vec.z);
  }

  private void applyQ(ArrayNode q) {
    Quaternion o = new Quaternion(
            (float) q.get(0).asDouble(),
            (float) q.get(1).asDouble(),
            (float) q.get(2).asDouble(),
            (float) q.get(3).asDouble());
    Matrix4 tmp = new Matrix4();
    o.toMatrix(tmp.val);
    Matrix3 qMat = new Matrix3();
    qMat.set(tmp);
    Matrix3 result = new Matrix3();
    result.set(rotation);
    result.mul(qMat);
    o.setFromMatrix(result);
    q.set(0, o.x);
    q.set(1, o.y);
    q.set(2, o.z);
    q.set(3, o.w);
  }

  private void applyNode(JsonNode node, TreeSet<String> roots) {
    if (node.has("id")) {
      roots.add(node.get("id").asText());
    }
    if (node.get("translation") instanceof ArrayNode translation) {
      apply(translation, 0);
    }
    if (node.get("rotation") instanceof ArrayNode rotation) {
      applyQ(rotation);
    }
    JsonNode parts = node.get("parts");
    if (parts instanceof ArrayNode pArray) {
      for (int j = 0; j < pArray.size(); j++) {
        JsonNode part = pArray.get(j);
        JsonNode bones = part.get("bones");
        if (bones instanceof ArrayNode bArray) {
          for (int k = 0; k < bArray.size(); k++) {
            JsonNode bone = bArray.get(k);
            if (bone.get("translation") instanceof ArrayNode translation) {
              apply(translation, 0);
            }
            if (bone.get("rotation") instanceof ArrayNode rotation) {
              applyQ(rotation);
            }
          }
        }
      }
    }
    JsonNode children = node.get("children");
    if (children instanceof ArrayNode cArray) {
      for (int i = 0; i < cArray.size(); i++) {
        // should not need to rotate childrne
        // applyNode(cArray.get(i));
      }
    }
  }
}
