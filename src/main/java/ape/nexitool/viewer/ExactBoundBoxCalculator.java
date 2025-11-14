package ape.nexitool.viewer;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FlushablePool;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExactBoundBoxCalculator {  // See helper below
  private static boolean integrate(Renderable r, MeshPart meshPart, Vector3 min, Vector3 max, Function<Vector3, Boolean> points) {
    final Vector3 localPos = new Vector3();
    final Vector3 skinnedPos = new Vector3();
    final Vector3 tmp = new Vector3();
    // extract vertices
    int stride = meshPart.mesh.getVertexSize() / 4;
    float[] vertices = new float[meshPart.mesh.getNumVertices() * stride];
    meshPart.mesh.getVertices(vertices);
    // dedupe the indices and select only for this mesh part
    TreeSet<Integer> used = new TreeSet<>();
    {
      short[] indices = new short[meshPart.mesh.getNumIndices()];
      meshPart.mesh.getIndices(indices);
      for (int i = 0; i < meshPart.size; i++) {
        used.add((int) indices[meshPart.offset + i]);
      }
    }
    // get bone offsets
    int posOffset = meshPart.mesh.getVertexAttribute(com.badlogic.gdx.graphics.VertexAttributes.Usage.Position).offset / 4;
    ArrayList<Integer> boneOffsets = new ArrayList<>();
    for (VertexAttribute a : meshPart.mesh.getVertexAttributes()) {
      if (a.usage == VertexAttributes.Usage.BoneWeight) {
        boneOffsets.add(a.offset / 4);
      }
    }
    Consumer<Vector3> consider = (p) -> {
      // Update bounds
      boolean allowed = true;
      if (points != null) {
        allowed = points.apply(p);
      }
      if (allowed) {
        min.x = Math.min(min.x, p.x);
        min.y = Math.min(min.y, p.y);
        min.z = Math.min(min.z, p.z);
        max.x = Math.max(max.x, p.x);
        max.y = Math.max(max.y, p.y);
        max.z = Math.max(max.z, p.z);
      }
    };
    boolean isSkinned = r.bones != null && r.bones.length > 0 && !boneOffsets.isEmpty();
    for (int index : used) {
      int i = index * stride;
      int p = i + posOffset;
      localPos.set(vertices[p], vertices[p + 1], vertices[p + 2]);
      if (isSkinned) {
        skinnedPos.set(0, 0, 0);
        float total = 0;
        for (int k = 0; k < boneOffsets.size(); k++) {
          int bw = i + boneOffsets.get(k);
          int idx = Math.min(r.bones.length - 1, Math.max(0, (int) vertices[bw]));
          float weight = vertices[bw + 1];
          total += weight;
          if (0 <= idx && idx < r.bones.length) {
            tmp.set(localPos).mul(r.bones[idx]).scl(weight);
            skinnedPos.add(tmp);
          } else {
            tmp.set(localPos).scl(weight);
          }
        }
        if (total > 0) skinnedPos.scl(1f / total);
        skinnedPos.mul(r.worldTransform);
        consider.accept(skinnedPos);
      } else {
        skinnedPos.set(localPos).mul(r.worldTransform);
        consider.accept(skinnedPos);
      }
    }
    return !used.isEmpty();
  }

  public static BoundingBox calculate(final ModelInstance instance, Function<Vector3, Boolean> points) {
    instance.calculateTransforms();  // Critical: updates node.worldTransform + instance.bones
    BoundingBox bounds = new BoundingBox();
    Vector3 min = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    Vector3 max = new Vector3(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
    boolean hasVerts = false;
    RenderablePool pool = new RenderablePool();
    Array<Renderable> renderables = new Array<>();
    instance.getRenderables(renderables, pool);
    for (Renderable r : renderables) {
      MeshPart meshPart = r.meshPart;
      if (meshPart == null || meshPart.mesh == null) continue;
      if (integrate(r, meshPart, min, max, points)) {
        hasVerts = true;
      }
    }
    pool.flush();
    renderables.clear();
    if (hasVerts) {
      bounds.min.set(min);
      bounds.max.set(max);
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
