package ape.nexitool.transforms;

import ape.nexitool.contracts.Transform;
import ape.nexitool.tools.json.ParsedVertexAttributes;
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
              verts.set(o + 0, verts.get(o + 0).doubleValue() + change.x);
              verts.set(o + 1, verts.get(o + 1).doubleValue() + change.y);
              verts.set(o + 2, verts.get(o + 2).doubleValue() + change.z);
            }
          }
        }
      }
    }
  }

  public void executeDynamic(ObjectNode root) {

  }

  @Override
  public void execute(ObjectNode root) {

  }
}
