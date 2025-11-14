package ape.nexitool.viewer;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FlushablePool;

public class BBox {  // See helper below
  /**
   * Computes the exact world-space bounding box by mirroring GPU skinning.
   * Uses Renderable system — same as ModelBatch.
   */
  public static BoundingBox calculateBoundingBoxWithBones(final ModelInstance instance) {
    instance.calculateTransforms();  // Critical: updates node.worldTransform + instance.bones
    BoundingBox bounds = new BoundingBox();
    Vector3 min = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    Vector3 max = new Vector3(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
    boolean hasVerts = false;
    RenderablePool pool = new RenderablePool();
    Array<Renderable> renderables = new Array<>();
    instance.getRenderables(renderables, pool);
    final Vector3 localPos = new Vector3();
    final Vector3 skinnedPos = new Vector3();
    final Vector3 tmp = new Vector3();
    final Matrix4 boneMat = new Matrix4();
    for (Renderable r : renderables) {
      MeshPart meshPart = r.meshPart;
      if (meshPart == null || meshPart.mesh == null) continue;
      // Extract vertices
      int vSize = (meshPart.mesh.getVertexSize() / 4);
      float[] vertices = new float[meshPart.mesh.getNumVertices() * vSize];
      meshPart.mesh.getVertices(vertices);
      int stride = meshPart.mesh.getVertexSize() / 4;
      int posOffset = meshPart.mesh.getVertexAttribute(com.badlogic.gdx.graphics.VertexAttributes.Usage.Position).offset / 4;
      VertexAttribute boneAttribute = meshPart.mesh.getVertexAttribute(com.badlogic.gdx.graphics.VertexAttributes.Usage.BoneWeight);
      int bwOffset = boneAttribute.offset / 4;
      float[] weights = new float[boneAttribute.numComponents];
      int[] indices = new int[boneAttribute.numComponents];

      boolean isSkinned = r.bones != null && r.bones.length > 0;
      for (int i = 0; i + bwOffset + 7 < vertices.length; i += stride) {
        int p = i + posOffset;
        localPos.set(vertices[p], vertices[p + 1], vertices[p + 2]);
        if (isSkinned) {
          skinnedPos.set(0, 0, 0);
          int bw = i + bwOffset;
          float total = 0;
          for (int k = 0; k < weights.length; k++) {
            weights[k] = vertices[bw + k];
            indices[k] = (int) vertices[bw + weights.length + k];
          }
          for (int inf = 0; inf < weights.length; inf++) {
            float weight = weights[inf];
            if (weight > 0.001f && 0 <= indices[inf] && indices[inf] < r.bones.length) {
              total += weights[inf];
              boneMat.set(r.bones[indices[inf]]);
              tmp.set(localPos).mul(boneMat).scl(weight);
              skinnedPos.add(tmp);
            }
          }
          if (total > 0) skinnedPos.scl(1f / total);
        } else {
          localPos.set(skinnedPos).mul(r.worldTransform);
        }
        // Update bounds
        min.x = Math.min(min.x, skinnedPos.x);
        min.y = Math.min(min.y, skinnedPos.y);
        min.z = Math.min(min.z, skinnedPos.z);
        max.x = Math.max(max.x, skinnedPos.x);
        max.y = Math.max(max.y, skinnedPos.y);
        max.z = Math.max(max.z, skinnedPos.z);
        hasVerts = true;
      }
    }
    // Clean up
    pool.flush();
    renderables.clear();
    if (hasVerts) {
      bounds.min.set(min);
      bounds.max.set(max);
      bounds.mul(instance.transform);
    } else {
      bounds.min.set(0, 0, 0);
      bounds.max.set(0, 0, 0);
    }
    return bounds;
  }

  protected static class RenderablePool extends FlushablePool<Renderable> {
    protected RenderablePool() {
    }

    protected Renderable newObject() {
      return new Renderable();
    }

    public Renderable obtain() {
      Renderable renderable = super.obtain();
      renderable.environment = null;
      renderable.material = null;
      renderable.meshPart.set("", null, 0, 0, 0);
      renderable.shader = null;
      renderable.userData = null;
      return renderable;
    }
  }
}
