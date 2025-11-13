package ape.nexitool.viewer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Tool {
  public static void main(String[] args) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("3D Model Viewer with Grids");
    config.setWindowedMode(1280, 720);
    config.useVsync(true);
    new Lwjgl3Application(new Scene("C:\\Users\\jeff\\github\\games\\royale\\assets\\itch\\kaykit\\character.druid.g3dj"), config);
  }
}