package ape.nexitool;

import ape.nexitool.tools.NormalizeMaterials;
import ape.nexitool.tools.Radial;
import ape.nexitool.tools.Resize;
import ape.nexitool.tools.Sizer;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
  private static void help() {
    System.out.println("nexitool - swiss army knike for nexidrive client engine");
    System.out.println("--------");
    System.out.println("nexitool help <- this screen");
    System.out.println("nexitool radial imagefile.png");
    System.out.println("nexitool get-size model.gd3j");
    System.out.println("nexitool norm-mat input.g3dj output.g3dj");
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
    if (args[0].equals("norm-mat")) {
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
      Resize.process(input, output, Double.parseDouble(args[3]));
      return;
    }
  }
}