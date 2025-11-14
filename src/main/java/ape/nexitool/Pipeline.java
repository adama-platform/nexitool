package ape.nexitool;

import ape.nexitool.transforms.*;
import ape.nexitool.transforms.support.Sizing;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Pipeline {
  public static void process(ObjectNode root, String[] args, int at) {
    if (at >= args.length) {
      return;
    }
    String command = args[at];
    int next = at + 1;
    switch (command) {
      case "enforce-static":
        new EnforceStatic().execute(root);
        System.out.println("mod: enforce static object");
        break;
      case "flip-index-winding-order":
        new FlipIndexWindingOrder().execute(root);
        System.out.println("mod: fliping index order");
        break;
      case "normal":
      case "normalize":
        new Normalize().execute(root);
        System.out.println("mod: normalizing materials");
        break;
      case "rotate-x": {
        double degrees = Double.parseDouble(args[next]);
        next++;
        Matrix3 mat = new Matrix3();
        mat.setToRotation(new Vector3(1, 0, 0), (float) degrees);
        new Rotate(mat).execute(root);
        System.out.println("mod: rotate x axis by " + degrees + " degrees");
        break;
      }
      case "rotate-y": {
        double degrees = Double.parseDouble(args[next]);
        next++;
        Matrix3 mat = new Matrix3();
        mat.setToRotation(new Vector3(0, 1, 0), (float) degrees);
        new Rotate(mat).execute(root);
        System.out.println("mod: rotate y axis by " + degrees + " degrees");
        break;
      }
      case "rotate-z": {
        double degrees = Double.parseDouble(args[next]);
        next++;
        Matrix3 mat = new Matrix3();
        mat.setToRotation(new Vector3(0, 0, 1), (float) degrees);
        new Rotate(mat).execute(root);
        System.out.println("mod: rotate z axis by " + degrees + " degrees");
        break;
      }
      case "resize":
      case "scale": {
        double scale = Double.parseDouble(args[next]);
        next++;
        new Scale((float) scale).execute(root);
        System.out.println("mod: scale " + scale + "x");
        break;
      }
      case "make-min-y-zero": {
        BoundingBox box = Sizing.of(root);
        new Translate(new Vector3(0, -box.min.y,0)).execute(root);
        break;
      }
      case "center": {
        BoundingBox box = Sizing.of(root);
        Vector3 center = box.min.cpy().add(box.max).scl(-0.5f);
        new Translate(center).execute(root);
        break;
      }
      case "center-base-xz": {
        float max_y = Float.parseFloat(args[next]);
        next++;
        BoundingBox box = Sizing.of(root, max_y);
        Vector3 center = box.min.cpy().add(box.max).scl(-0.5f);
        center.y = 0;
        new Translate(center).execute(root);
        break;
      }
      case "set-min-y": {
        float min_y = Float.parseFloat(args[next]);
        next++;
        BoundingBox box = Sizing.of(root);
        new Translate(new Vector3(0, min_y - box.min.y, 0)).execute(root);
        break;
      }
    }
    process(root, args, next);
  }
}
