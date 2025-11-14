package ape.nexitool.transforms.support;

import ape.nexitool.viewer.ExactBoundBoxCalculator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Path;

public class Sizing {

  public static final BoundingBox of(ObjectNode root) {
    String file = "__temp.g3dj";
    try {
      Files.writeString(Path.of(file), root.toPrettyString());
      G3dModelLoader loader = new G3dModelLoader(new JsonReader());
      Model model = loader.loadModel(Gdx.files.internal(file));
      ModelInstance instance = new ModelInstance(model);
      BoundingBox box = ExactBoundBoxCalculator.calculate(instance, null);
      model.dispose();
      Files.delete(Path.of(file));
      return box;
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static final BoundingBox of(ObjectNode root, float max_y) {
    String file = "__temp.g3dj";
    try {
      Files.writeString(Path.of(file), root.toPrettyString());
      G3dModelLoader loader = new G3dModelLoader(new JsonReader());
      Model model = loader.loadModel(Gdx.files.internal(file));
      ModelInstance instance = new ModelInstance(model);
      BoundingBox box = ExactBoundBoxCalculator.calculate(instance, null);
      model.dispose();
      Files.delete(Path.of(file));
      return box;
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }
}
