package ape.nexitool;

import ape.nexitool.tools.*;
import ape.nexitool.tools.Sizer;
import ape.nexitool.viewer.Tool;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
  private static void help() {
    System.out.println("nexitool - swiss army knike for nexidrive client engine");
    System.out.println("--------");
    System.out.println("nexitool help <- this screen");
    System.out.println("nexitool radial imagefile.png");
    System.out.println("nexitool get-size model.gd3j");
    System.out.println("nexitool norm-materials input.g3dj output.g3dj");
    System.out.println("nexitool resize input.g3dj output.g3dj scale");
    System.out.println("nexitool fitbox input.g3dj output.g3dj size");
    System.out.println("nexitool copy-animations animation.g3dj model.g3dj output.g3dj");
    System.out.println("nexitool list-animations animation.g3dj");
    System.out.println("nexitool norm-animations input.g3dj output.g3dj");
  }
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      help();
      return;
    }
    if (args[0].equals("help")) {
      help();
      return;
    }
    if (args[0].equals("radial")) {
      if (args.length != 2) {
        help();
        return;
      }
      Radial.process(args[1], null);
      return;
    }
    if (args[0].equals("get-size")) {
      if (args.length != 2) {
        help();
        return;
      }
      Sizer.process(args[1]);
    }
    if (args[0].equals("norm-materials")) {
      if (args.length != 3) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      NormalizeMaterials.process(input, output);
      return;
    }
    if (args[0].equals("resize")) {
      if (args.length != 4) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      Scale.process(input, output, Double.parseDouble(args[3]));
      return;
    }
    if (args[0].equals("center-base")) {
      if (args.length != 4) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      CenterBase.process(input, output, Double.parseDouble(args[3]));
      return;
    }
    if (args[0].equals("rotate-y-180")) {
      if (args.length != 3) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      RotateY180.process(input, output);
      return;
    }
    if (args[0].equals("ensure-static")) {
      if (args.length != 3) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      EnsureStatic.process(input, output);
      return;
    }
    if (args[0].equals("rotate-x-90")) {
      if (args.length != 3) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      RotateX90.process(input, output);
      return;
    }
    if (args[0].equals("rotate-z-90")) {
      if (args.length != 3) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      RotateZ90.process(input, output);
      return;
    }
    if (args[0].equals("fit-box")) {
      if (args.length != 4) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      FitBox.process(input, output, Double.parseDouble(args[3]));
      return;
    }
    if (args[0].equals("center")) {
      if (args.length != 3) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      Center.process(input, output);
      return;
    }
    if (args[0].equals("set-y-center-xz")) {
      if (args.length != 4) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      SetYAndCenterXZ.process(input, output, Double.parseDouble(args[3]));
      return;
    }
    if (args[0].equals("copy-animations")) {
      if (args.length != 4) {
        help();
        return;
      }
      String animation = args[1];
      String model = args[2];
      String output = args[3];
      CopyAnimations.process(animation, model, output);
      return;
    }
    if (args[0].equals("viewer")) {
      Tool.main(args);
      return;
    }
    if (args[0].equals("list-animations")) {
      if (args.length != 2) {
        help();
        return;
      }
      String model = args[1];
      ListAnimations.process(model);
    }
    if (args[0].equals("norm-animations")) {
      if (args.length != 3) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      NormalizeAnimations.process(input, output);
    }
    if (args[0].equals("flip-indicies")) {
      if (args.length != 3) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      FlipIndices.process(input, output);
    }
    if (args[0].equals("align-floor")) {
      if (args.length != 3) {
        help();
        return;
      }
      String input = args[1];
      String output = args[2];
      AlignWithFloor.process(input, output);
    }
  }
}