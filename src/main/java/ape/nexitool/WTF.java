package ape.nexitool;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;

public class WTF {
  public static void main(String[] args) {
    Vector3 p = new Vector3(1, 2, 3);
    Matrix3 mat = new Matrix3();
    mat.setToRotation(new Vector3(1, 0, 0), 90);
    System.out.println(p);
    System.out.println(p.mul(mat));
  }
}
