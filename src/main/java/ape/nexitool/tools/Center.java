package ape.nexitool.tools;

import ape.nexitool.tools.json.VertexAttributes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Center {

  private static class Vertex {
    public final double x;
    public final double y;
    public final double z;
    public Vertex(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    @Override
    public String toString() {
      return "Vertex{" +
              "x=" + x +
              ", y=" + y +
              ", z=" + z +
              '}';
    }
  }

  private static class Box {
    public double min_x, min_y, min_z;
    public double max_x, max_y, max_z;

    public Box() {
      this.min_x = Double.POSITIVE_INFINITY;
      this.min_y = Double.POSITIVE_INFINITY;
      this.min_z = Double.POSITIVE_INFINITY;
      this.max_x = Double.NEGATIVE_INFINITY;
      this.max_y = Double.NEGATIVE_INFINITY;
      this.max_z = Double.NEGATIVE_INFINITY;
    }

    public void add(Vertex v) {
      min_x = Math.min(min_x, v.x);
      min_y = Math.min(min_y, v.y);
      min_z = Math.min(min_z, v.z);
      max_x = Math.max(max_x, v.x);
      max_y = Math.max(max_y, v.y);
      max_z = Math.max(max_z, v.z);
    }

    public Box cloneWithTranslation(double x, double y, double z) {
      Box next = new Box();
      next.min_x = min_x + x;
      next.min_y = min_y + y;
      next.min_z = min_z + z;
      next.max_x = max_x + x;
      next.max_y = max_y + y;
      next.max_z = max_z + z;
      return next;
    }

    public Box(Box a, Box b) {
      this.min_x = Math.min(a.min_x, b.min_x);
      this.min_y = Math.min(a.min_y, b.min_y);
      this.min_z = Math.min(a.min_z, b.min_z);
      this.max_x = Math.max(a.max_x, b.max_x);
      this.max_y = Math.max(a.max_y, b.max_y);
      this.max_z = Math.max(a.max_z, b.max_z);
    }

    @Override
    public String toString() {
      return "Box{" +
              " min_x=" + min_x +
              ", min_y=" + min_y +
              ", min_z=" + min_z +
              ", max_x=" + max_x +
              ", max_y=" + max_y +
              ", max_z=" + max_z +
              '}';
    }
  }

  public static TreeMap<Integer, Vertex> extractVertices(JsonNode mesh) {
    TreeMap<Integer, Vertex> map = new TreeMap<Integer, Vertex>();
    int id = 0;
    VertexAttributes attributes = new VertexAttributes(mesh);
    if (attributes.position >= 0) {
      ArrayNode vertices = (ArrayNode) mesh.get("vertices");
      for (int v = 0; v + attributes.stride - 1 < vertices.size(); v += attributes.stride) {
        double x = vertices.get(v + attributes.position + 0).asDouble();
        double y = vertices.get(v + attributes.position + 1).asDouble();
        double z = vertices.get(v + attributes.position + 2).asDouble();
        map.put(id, new Vertex(x, y, z));
        id++;
      }
    }
    return map;
  }

  public static TreeMap<String, Box> extractParts(JsonNode node) {
    TreeMap<String, Box> map = new TreeMap<String, Box>();
    ArrayNode meshes = (ArrayNode) node.get("meshes");
    for (int k = 0; k < meshes.size(); k++) {
      JsonNode mesh = meshes.get(k);
      TreeMap<Integer, Vertex> vertices = extractVertices(mesh);
      ArrayNode parts = (ArrayNode) mesh.get("parts");
      for (int p = 0; p < parts.size(); p++) {
        ObjectNode part = (ObjectNode) parts.get(p);
        String id = part.get("id").asText();
        Box box = new Box();
        ArrayNode indices = (ArrayNode) part.get("indices");
        for (int i = 0; i < indices.size(); i++) {
          Vertex v = vertices.get(indices.get(i).asInt());
          if (v != null) {
            box.add(v);
          }
        }
        map.put(id, box);
      }
    }
    return map;
  }

  public static TreeMap<String, ArrayList<Box>>  buildBoneMap(JsonNode root, TreeMap<String, Box> boxes) {
    TreeMap<String, ArrayList<Box>> bonesToBoxes = new TreeMap<String, ArrayList<Box>>();;
    ArrayNode nodes = (ArrayNode) root.get("nodes");
    for (int k = 0; k < nodes.size(); k++) {
      JsonNode node = nodes.get(k);
      ArrayNode translation = (ArrayNode) node.get("translation");
      ArrayNode parts = (ArrayNode) node.get("parts");
      if (parts != null) {
        for (int p = 0; p < parts.size(); p++) {
          ObjectNode part = (ObjectNode) parts.get(p);
          if (part.has("meshpartid")) {
            String lookup = part.get("meshpartid").asText();
            Box box = boxes.get(lookup);
            if (part.has("bones") && box != null) {
              ArrayNode bones = (ArrayNode) part.get("bones");
              for (int b = 0; b < bones.size(); b++) {
                ObjectNode bone = (ObjectNode) bones.get(b);
                if (bone.has("node")) {
                  String boneId = bone.get("node").asText();
                  ArrayList<Box> boneBoxes = bonesToBoxes.get(boneId);
                  if (boneBoxes == null) {
                    boneBoxes = new ArrayList<Box>();
                    bonesToBoxes.put(boneId, boneBoxes);
                  }
                  boneBoxes.add(box);
                }
              }
            }
          }
        }
      }
    }
    return bonesToBoxes;
  }

  public static void translateNodes(JsonNode root, double cx, double cy, double cz) {
    ArrayNode nodes = (ArrayNode) root.get("nodes");
    for (int k = 0; k < nodes.size(); k++) {
      JsonNode node = nodes.get(k);
      ArrayNode translation = (ArrayNode) node.get("translation");
      if (translation != null) {
        translation.set(0, translation.get(0).asDouble() - cx);
        translation.set(1, translation.get(1).asDouble() - cy);
        translation.set(2, translation.get(2).asDouble() - cz);
      }
    }
  }


  // Helper function to multiply two quaternions (parent * local)
  public static double[] multiplyQuat(double ax, double ay, double az, double aw,
                                      double bx, double by, double bz, double bw) {
    double rx = aw * bx + ax * bw + ay * bz - az * by;
    double ry = aw * by - ax * bz + ay * bw + az * bx;
    double rz = aw * bz + ax * by - ay * bx + az * bw;
    double rw = aw * bw - ax * bx - ay * by - az * bz;
    return new double[]{rx, ry, rz, rw};
  }

  // Helper function to rotate a vector by a quaternion using rotation matrix equivalent
// (assumes quaternion is normalized; efficient and avoids pure quaternion math issues)
  public static Vertex rotateByQuat(double qx, double qy, double qz, double qw,
                                    double vx, double vy, double vz) {
    double xx = qx * qx;
    double yy = qy * qy;
    double zz = qz * qz;
    double xy = qx * qy;
    double xz = qx * qz;
    double xw = qx * qw;
    double yz = qy * qz;
    double yw = qy * qw;
    double zw = qz * qw;

    double m00 = 1 - 2 * (yy + zz);
    double m01 = 2 * (xy - zw);
    double m02 = 2 * (xz + yw);

    double m10 = 2 * (xy + zw);
    double m11 = 1 - 2 * (xx + zz);
    double m12 = 2 * (yz - xw);

    double m20 = 2 * (xz - yw);
    double m21 = 2 * (yz + xw);
    double m22 = 1 - 2 * (xx + yy);

    double rx = m00 * vx + m01 * vy + m02 * vz;
    double ry = m10 * vx + m11 * vy + m12 * vz;
    double rz = m20 * vx + m21 * vy + m22 * vz;

    return new Vertex(rx, ry, rz);
  }

  // Modified recursive function to handle accumulated position and rotation
  public static void buildBonesRecursive(TreeMap<String, Vertex> positions, JsonNode node, Set<String> isBoneSet,
                                         double x, double y, double z, double qx, double qy, double qz, double qw) {
    // Local translation (default to 0 if absent)
    double tx = 0, ty = 0, tz = 0;
    if (node.has("translation")) {
      ArrayNode translation = (ArrayNode) node.get("translation");
      tx = translation.get(0).asDouble();
      ty = translation.get(1).asDouble();
      tz = translation.get(2).asDouble();
    }

    // Rotate local translation by parent's accumulated rotation
    Vertex rotated = rotateByQuat(qx, qy, qz, qw, tx, ty, tz);

    // Accumulate position
    double nx = x + rotated.x;
    double ny = y + rotated.y;
    double nz = z + rotated.z;

    // Local rotation (default to identity if absent)
    double lqx = 0, lqy = 0, lqz = 0, lqw = 1;
    if (node.has("rotation")) {
      ArrayNode rotation = (ArrayNode) node.get("rotation");
      lqx = rotation.get(0).asDouble();
      lqy = rotation.get(1).asDouble();
      lqz = rotation.get(2).asDouble();
      lqw = rotation.get(3).asDouble();
    }

    // Accumulate rotation: parent * local
    double[] newq = multiplyQuat(qx, qy, qz, qw, lqx, lqy, lqz, lqw);
    double nqx = newq[0];
    double nqy = newq[1];
    double nqz = newq[2];
    double nqw = newq[3];

    // Store if it's a bone
    if (node.has("id")) {
      String id = node.get("id").asText();
      if (isBoneSet.contains(id)) {
        positions.put(id, new Vertex(nx, ny, nz));
      }
    }

    // Recurse to children with accumulated position and rotation
    if (node.has("children")) {
      ArrayNode children = (ArrayNode) node.get("children");
      for (int k = 0; k < children.size(); k++) {
        JsonNode child = children.get(k);
        buildBonesRecursive(positions, child, isBoneSet, nx, ny, nz, nqx, nqy, nqz, nqw);
      }
    }
  }

  // Modified entry point (start with identity rotation: q = [0,0,0,1])
  public static TreeMap<String, Vertex> buildBones(JsonNode root, Set<String> isBoneSet) {
    TreeMap<String, Vertex> positions = new TreeMap<String, Vertex>();
    ArrayNode nodes = (ArrayNode) root.get("nodes");
    for (int k = 0; k < nodes.size(); k++) {
      JsonNode node = nodes.get(k);
      buildBonesRecursive(positions, node, isBoneSet, 0, 0, 0, 0, 0, 0, 1);
    }
    return positions;
  }


  public static void process(String inputPath, String outputPath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    TreeMap<String, Box> boxes = extractParts(root);
    TreeMap<String, ArrayList<Box>> boxesToBoxes = buildBoneMap(root, boxes);
    TreeMap<String, Vertex> bones = buildBones(root, boxesToBoxes.keySet());
    Box big = null;
    for (Map.Entry<String, Vertex> entry : bones.entrySet()) {
      Vertex vertex = entry.getValue();
      String bone = entry.getKey();
      ArrayList<Box> boneBoxes = boxesToBoxes.get(bone);
      if (boneBoxes != null) {
        for (Box box : boneBoxes) {
          Box toAdd = box.cloneWithTranslation(vertex.x, vertex.y, vertex.z);
          System.out.println("ADD:" + toAdd + " via " + bone + "/" + vertex);
          if (big == null) {
            big = toAdd;
          } else {
            big = new Box(big, toAdd);
          }
        }
      }
    }


    System.out.println("TOTAL:" + big);

    double cx = (big.min_x + big.max_x) / 2.0;
    double cy = (big.min_y + big.max_y) / 2.0;
    double cz = (big.min_z + big.max_z) / 2.0;

    System.out.println("BIG:" + big);
    translateNodes(root, cx, cy, cz);
    System.out.println("CX: " + Math.round(cx * 10) / 10.0 + " CY: " + Math.round(cy * 10) / 10.0 + " CZ: " + Math.round(cz * 10) / 10.0);

    Files.writeString(Paths.get(outputPath), root.toPrettyString());
  }
}
