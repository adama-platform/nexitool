package ape.nexitool.viewer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;

public class Scene extends ApplicationAdapter {
  private final String file;
  private PerspectiveCamera cam;
  private CameraInputController camController;
  private ModelBatch modelBatch;
  private Environment environment;
  private Model xGridModel, yGridModel, zGridModel;
  private ModelInstance xGridInstance, yGridInstance, zGridInstance;
  private Model sample;
  private ModelInstance sampleI;
  private Model pointCloud;
  private ModelInstance pointCloudI;
  // Configurable constants for grid planes
  private float C = 0f; // x = C (YZ plane)
  private float D = 0f; // y = D (XZ plane)
  private float E = 0f; // z = E (XY plane)

  public Scene(String file) {
    this.file = file;
  }

  @Override
  public void create() {
    // Setup rendering
    modelBatch = new ModelBatch();
    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    // Setup camera
    cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.position.set(5f, 5f, 5f);
    cam.lookAt(0, 0, 0);
    cam.near = 0.1f;
    cam.far = 100f;
    cam.update();
    // Camera controller for mouse rotation/orbit around origin
    camController = new CameraInputController(cam);
    Gdx.input.setInputProcessor(camController);
    G3dModelLoader loader = new G3dModelLoader(file.endsWith(".g3db") ? new UBJsonReader() : new JsonReader());
    sample = loader.loadModel(Gdx.files.internal(file));
    sampleI = new ModelInstance(sample);
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder part = modelBuilder.part("cloud", GL20.GL_POINTS, VertexAttributes.Usage.Position, new Material("a"));
    part.setColor(Color.CYAN);
    BoundingBox box = ExactBoundBoxCalculator.calculate(sampleI, (p) -> {
      part.index(part.vertex(p.x, p.y, p.z));
      return true;
    });
    pointCloud = modelBuilder.end();
    pointCloudI = new ModelInstance(pointCloud);
    C = box.min.x;
    D = box.min.y;
    E = box.min.z;
    System.out.println("C = " + C);
    System.out.println("D = " + D);
    System.out.println("E = " + E);
    // Create grid models
    xGridModel = createPlaneGrid(C, "yz", 10, 10f, Color.BLUE);
    yGridModel = createPlaneGrid(D, "xz", 10, 10f, Color.GREEN);
    zGridModel = createPlaneGrid(E, "xy", 10, 10f, Color.RED);
    xGridInstance = new ModelInstance(xGridModel);
    yGridInstance = new ModelInstance(yGridModel);
    zGridInstance = new ModelInstance(zGridModel);
  }

  private Model createPlaneGrid(float constant, String plane, int divisions, float size, Color color) {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder meshBuilder = modelBuilder.part("grid", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
    meshBuilder.setColor(color);
    float halfSize = size / 2f;
    float step = size / divisions;
    for (int i = 0; i <= divisions; i++) {
      float pos = -halfSize + (i * step);
      if ("yz".equals(plane)) { // YZ plane (x = constant)
        // Lines parallel to Y (fixed Z)
        meshBuilder.line(constant, -halfSize, pos, constant, halfSize, pos);
        // Lines parallel to Z (fixed Y)
        meshBuilder.line(constant, pos, -halfSize, constant, pos, halfSize);
      } else if ("xz".equals(plane)) { // XZ plane (y = constant)
        // Lines parallel to X (fixed Z)
        meshBuilder.line(-halfSize, constant, pos, halfSize, constant, pos);
        // Lines parallel to Z (fixed X)
        meshBuilder.line(pos, constant, -halfSize, pos, constant, halfSize);
      } else if ("xy".equals(plane)) { // XY plane (z = constant)
        // Lines parallel to X (fixed Y)
        meshBuilder.line(-halfSize, pos, constant, halfSize, pos, constant);
        // Lines parallel to Y (fixed X)
        meshBuilder.line(pos, -halfSize, constant, pos, halfSize, constant);
      }
    }
    return modelBuilder.end();
  }

  @Override
  public void render() {
    camController.update();
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    modelBatch.begin(cam);
    modelBatch.render(xGridInstance, environment);
    modelBatch.render(yGridInstance, environment);
    modelBatch.render(zGridInstance, environment);
    sampleI.transform.idt();
    modelBatch.render(sampleI, environment);
    if (pointCloudI != null) {
      modelBatch.render(pointCloudI, environment);
    }
    // Add your 3D model here, e.g.:
    // modelBatch.render(yourModelInstance, environment);
    modelBatch.end();
  }

  @Override
  public void resize(int width, int height) {
    cam.viewportWidth = width;
    cam.viewportHeight = height;
    cam.update();
  }

  @Override
  public void dispose() {
    modelBatch.dispose();
    xGridModel.dispose();
    yGridModel.dispose();
    zGridModel.dispose();
    sample.dispose();
  }
}