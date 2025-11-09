package ape.nexitool;

import ape.nexitool.tools.Normalize;
import ape.nexitool.tools.Radial;
import ape.nexitool.tools.Sizer;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
  private static void help() {
    System.out.println("nexitool - swiss army knike for nexidrive client engine");
    System.out.println("--------");
    System.out.println("nexitool help <- this screen");
    System.out.println("nexitool radial imagefile <- build a radial function for the given image file");
    System.out.println("nexitool size model.gd3j <- return the sizing of the gdx model (in json format)");

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
    if (args[0].equals("size")) {
      if (args.length != 2) {
        help();
        return;
      }
      Sizer.process(args[1]);
    }
    if (args[0].equals("normalize")) {
      if (args.length != 2) {
        help();
        return;
      }
      Normalize.process(args[1]);
    }
  }
}