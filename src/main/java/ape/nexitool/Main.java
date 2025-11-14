package ape.nexitool;

import ape.nexitool.tools.CopyAnimations;
import ape.nexitool.tools.ListAnimations;
import ape.nexitool.tools.Radial;
import ape.nexitool.transforms.support.MockGL;
import ape.nexitool.viewer.Tool;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
  private static void help() {
    System.out.println("nexitool - swiss army knife for nexidrive client engine");
    System.out.println("--------");
    System.out.println("nexitool pipeline filename COMMAND-STREAM");
    System.out.println("  COMMAND-STREAM are composoed of function (+args) with these possibilities:");
    System.out.println("  coming soon");
  }

  public static void pipeline(String[] args) {
    // 1. Minimal config
    HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
    Gdx.gl = Gdx.gl20 = new MockGL();
    // 2. Your app logic
    ApplicationListener listener = new ApplicationListener() {
      @Override
      public void create() {
        try {
          Path pathToModify = Paths.get(args[1]);
          String json = Files.readString(pathToModify);
          System.out.println("read:" + args[1]);
          ObjectMapper mapper = new ObjectMapper();
          JsonNode root = mapper.readTree(json);
          Pipeline.process((ObjectNode) root, args, 2);
          Files.writeString(pathToModify, mapper.writeValueAsString(root));
          System.out.println("wrote:" + args[1]);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          Gdx.app.exit();
        }
      }

      @Override
      public void resize(int width, int height) {
      }

      @Override
      public void render() {
      }

      @Override
      public void pause() {
      }

      @Override
      public void resume() {
      }

      @Override
      public void dispose() {
      }
    };
    new HeadlessApplication(listener, config);
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
    if (args[0].equals("g3dj-mod") || args[0].equals("pipeline")) {
      if (args.length > 2) {
        pipeline(args);
      }
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
  }
}