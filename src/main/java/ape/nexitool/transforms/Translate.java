package ape.nexitool.transforms;

import ape.nexitool.contracts.Transform;
import ape.nexitool.transforms.support.ParsedVertexAttributes;
import ape.nexitool.transforms.support.TestStatic;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Translate implements Transform {
  public final Vector3 change;

  public Translate(Vector3 change) {
    this.change = change;
  }

  public void executeStatic(ObjectNode root) {
    JsonNode meshesNode = root.get("meshes");
    if (meshesNode instanceof ArrayNode meshes) {
      for (int m = 0; m < meshes.size(); m++) {
        JsonNode mesh = meshes.get(m);
        ParsedVertexAttributes attributes = ParsedVertexAttributes.fromMesh(mesh);
        JsonNode vertsNode = mesh.get("vertices");
        if (attributes != null && vertsNode instanceof ArrayNode verts) {
          if (attributes.position >= 0) {
            for (int i = 0; i < verts.size(); i += attributes.stride) {
              int o = i + attributes.position;
              verts.set(o, verts.get(o).doubleValue() + change.x);
              verts.set(o + 1, verts.get(o + 1).doubleValue() + change.y);
              verts.set(o + 2, verts.get(o + 2).doubleValue() + change.z);
            }
          }
        }
      }
    }
  }

  public void executeDynamic(ObjectNode root) {
    ArrayNode nodes = (ArrayNode) root.get("nodes");
    for (int k = 0; k < nodes.size(); k++) {
      ObjectNode node = (ObjectNode) nodes.get(k);
      if (node.has("translation")) { // it is ignored, so we simply remove it
        node.remove("translation");
      }
      if (node.has("children")) {
        ArrayNode children = (ArrayNode) node.get("children");
        for (int i = 0; i < children.size(); i++) {
          ObjectNode child = (ObjectNode) children.get(i);
          ArrayNode translation = (ArrayNode) child.get("translations");
          if (translation == null) {
            translation = child.putArray("translation");
          }
          while (translation.size() < 3) {
            translation.add(0.0);
          }
          translation.set(0, translation.get(0).doubleValue() + change.x);
          translation.set(1, translation.get(1).doubleValue() + change.y);
          translation.set(2, translation.get(2).doubleValue() + change.z);
        }
      }
    }
  }

  @Override
  public void execute(ObjectNode root) {
    if (TestStatic.is(root)) {
      executeStatic(root);
    } else {
      executeDynamic(root);
    }
  }
}
