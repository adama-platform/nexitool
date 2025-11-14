package ape.nexitool;

import ape.nexitool.tools.*;
import ape.nexitool.tools.Sizer;
import ape.nexitool.tools.defunct.*;
import ape.nexitool.transforms.Normalize;
import ape.nexitool.viewer.Tool;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
  private static void help() {
    System.out.println("nexitool - swiss army knike for nexidrive client engine");
    System.out.println("--------");
    System.out.println("nexitool g3dj-mod filename COMMAND-STREAM");
    System.out.println("  COMMAND-STREAM are composoed of function (+args) with these possibilities:");



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

  public static void pipeline(String[] args) {
    // 1. Minimal config
    HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
    Gdx.gl = Gdx.gl20 = new GL20() {
      @Override
      public void glActiveTexture(int i) {
      }

      @Override
      public void glBindTexture(int i, int i1) {
      }

      @Override
      public void glBlendFunc(int i, int i1) {
      }

      @Override
      public void glClear(int i) {
      }

      @Override
      public void glClearColor(float v, float v1, float v2, float v3) {
      }

      @Override
      public void glClearDepthf(float v) {
      }

      @Override
      public void glClearStencil(int i) {
      }

      @Override
      public void glColorMask(boolean b, boolean b1, boolean b2, boolean b3) {
      }

      @Override
      public void glCompressedTexImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, Buffer buffer) {
      }

      @Override
      public void glCompressedTexSubImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer) {
      }

      @Override
      public void glCopyTexImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
      }

      @Override
      public void glCopyTexSubImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
      }

      @Override
      public void glCullFace(int i) {
      }

      @Override
      public void glDeleteTextures(int i, IntBuffer intBuffer) {
      }

      @Override
      public void glDeleteTexture(int i) {
      }

      @Override
      public void glDepthFunc(int i) {
      }

      @Override
      public void glDepthMask(boolean b) {
      }

      @Override
      public void glDepthRangef(float v, float v1) {
      }

      @Override
      public void glDisable(int i) {
      }

      @Override
      public void glDrawArrays(int i, int i1, int i2) {
      }

      @Override
      public void glDrawElements(int i, int i1, int i2, Buffer buffer) {
      }

      @Override
      public void glEnable(int i) {
      }

      @Override
      public void glFinish() {
      }

      @Override
      public void glFlush() {
      }

      @Override
      public void glFrontFace(int i) {
      }

      @Override
      public void glGenTextures(int i, IntBuffer intBuffer) {
      }

      @Override
      public int glGenTexture() {
        return 0;
      }

      @Override
      public int glGetError() {
        return 0;
      }

      @Override
      public void glGetIntegerv(int i, IntBuffer intBuffer) {
      }

      @Override
      public String glGetString(int i) {
        return "";
      }

      @Override
      public void glHint(int i, int i1) {
      }

      @Override
      public void glLineWidth(float v) {
      }

      @Override
      public void glPixelStorei(int i, int i1) {
      }

      @Override
      public void glPolygonOffset(float v, float v1) {
      }

      @Override
      public void glReadPixels(int i, int i1, int i2, int i3, int i4, int i5, Buffer buffer) {
      }

      @Override
      public void glScissor(int i, int i1, int i2, int i3) {
      }

      @Override
      public void glStencilFunc(int i, int i1, int i2) {
      }

      @Override
      public void glStencilMask(int i) {
      }

      @Override
      public void glStencilOp(int i, int i1, int i2) {
      }

      @Override
      public void glTexImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer) {
      }

      @Override
      public void glTexParameterf(int i, int i1, float v) {
      }

      @Override
      public void glTexSubImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer) {
      }

      @Override
      public void glViewport(int i, int i1, int i2, int i3) {
      }

      @Override
      public void glAttachShader(int i, int i1) {
      }

      @Override
      public void glBindAttribLocation(int i, int i1, String s) {
      }

      @Override
      public void glBindBuffer(int i, int i1) {
      }

      @Override
      public void glBindFramebuffer(int i, int i1) {
      }

      @Override
      public void glBindRenderbuffer(int i, int i1) {
      }

      @Override
      public void glBlendColor(float v, float v1, float v2, float v3) {
      }

      @Override
      public void glBlendEquation(int i) {
      }

      @Override
      public void glBlendEquationSeparate(int i, int i1) {
      }

      @Override
      public void glBlendFuncSeparate(int i, int i1, int i2, int i3) {
      }

      @Override
      public void glBufferData(int i, int i1, Buffer buffer, int i2) {
      }

      @Override
      public void glBufferSubData(int i, int i1, int i2, Buffer buffer) {
      }

      @Override
      public int glCheckFramebufferStatus(int i) {
        return 0;
      }

      @Override
      public void glCompileShader(int i) {
      }

      @Override
      public int glCreateProgram() {
        return 0;
      }

      @Override
      public int glCreateShader(int i) {
        return 0;
      }

      @Override
      public void glDeleteBuffer(int i) {
      }

      @Override
      public void glDeleteBuffers(int i, IntBuffer intBuffer) {
      }

      @Override
      public void glDeleteFramebuffer(int i) {
      }

      @Override
      public void glDeleteFramebuffers(int i, IntBuffer intBuffer) {
      }

      @Override
      public void glDeleteProgram(int i) {
      }

      @Override
      public void glDeleteRenderbuffer(int i) {
      }

      @Override
      public void glDeleteRenderbuffers(int i, IntBuffer intBuffer) {
      }

      @Override
      public void glDeleteShader(int i) {
      }

      @Override
      public void glDetachShader(int i, int i1) {
      }

      @Override
      public void glDisableVertexAttribArray(int i) {
      }

      @Override
      public void glDrawElements(int i, int i1, int i2, int i3) {
      }

      @Override
      public void glEnableVertexAttribArray(int i) {
      }

      @Override
      public void glFramebufferRenderbuffer(int i, int i1, int i2, int i3) {
      }

      @Override
      public void glFramebufferTexture2D(int i, int i1, int i2, int i3, int i4) {
      }

      @Override
      public int glGenBuffer() {
        return 0;
      }

      @Override
      public void glGenBuffers(int i, IntBuffer intBuffer) {
      }

      @Override
      public void glGenerateMipmap(int i) {
      }

      @Override
      public int glGenFramebuffer() {
        return 0;
      }

      @Override
      public void glGenFramebuffers(int i, IntBuffer intBuffer) {
      }

      @Override
      public int glGenRenderbuffer() {
        return 0;
      }

      @Override
      public void glGenRenderbuffers(int i, IntBuffer intBuffer) {
      }

      @Override
      public String glGetActiveAttrib(int i, int i1, IntBuffer intBuffer, IntBuffer intBuffer1) {
        return "";
      }

      @Override
      public String glGetActiveUniform(int i, int i1, IntBuffer intBuffer, IntBuffer intBuffer1) {
        return "";
      }

      @Override
      public void glGetAttachedShaders(int i, int i1, Buffer buffer, IntBuffer intBuffer) {
      }

      @Override
      public int glGetAttribLocation(int i, String s) {
        return 0;
      }

      @Override
      public void glGetBooleanv(int i, Buffer buffer) {
      }

      @Override
      public void glGetBufferParameteriv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public void glGetFloatv(int i, FloatBuffer floatBuffer) {
      }

      @Override
      public void glGetFramebufferAttachmentParameteriv(int i, int i1, int i2, IntBuffer intBuffer) {
      }

      @Override
      public void glGetProgramiv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public String glGetProgramInfoLog(int i) {
        return "";
      }

      @Override
      public void glGetRenderbufferParameteriv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public void glGetShaderiv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public String glGetShaderInfoLog(int i) {
        return "";
      }

      @Override
      public void glGetShaderPrecisionFormat(int i, int i1, IntBuffer intBuffer, IntBuffer intBuffer1) {
      }

      @Override
      public void glGetTexParameterfv(int i, int i1, FloatBuffer floatBuffer) {
      }

      @Override
      public void glGetTexParameteriv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public void glGetUniformfv(int i, int i1, FloatBuffer floatBuffer) {
      }

      @Override
      public void glGetUniformiv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public int glGetUniformLocation(int i, String s) {
        return 0;
      }

      @Override
      public void glGetVertexAttribfv(int i, int i1, FloatBuffer floatBuffer) {
      }

      @Override
      public void glGetVertexAttribiv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public void glGetVertexAttribPointerv(int i, int i1, Buffer buffer) {
      }

      @Override
      public boolean glIsBuffer(int i) {
        return false;
      }

      @Override
      public boolean glIsEnabled(int i) {
        return false;
      }

      @Override
      public boolean glIsFramebuffer(int i) {
        return false;
      }

      @Override
      public boolean glIsProgram(int i) {
        return false;
      }

      @Override
      public boolean glIsRenderbuffer(int i) {
        return false;
      }

      @Override
      public boolean glIsShader(int i) {
        return false;
      }

      @Override
      public boolean glIsTexture(int i) {
        return false;
      }

      @Override
      public void glLinkProgram(int i) {
      }

      @Override
      public void glReleaseShaderCompiler() {
      }

      @Override
      public void glRenderbufferStorage(int i, int i1, int i2, int i3) {
      }

      @Override
      public void glSampleCoverage(float v, boolean b) {
      }

      @Override
      public void glShaderBinary(int i, IntBuffer intBuffer, int i1, Buffer buffer, int i2) {
      }

      @Override
      public void glShaderSource(int i, String s) {
      }

      @Override
      public void glStencilFuncSeparate(int i, int i1, int i2, int i3) {
      }

      @Override
      public void glStencilMaskSeparate(int i, int i1) {
      }

      @Override
      public void glStencilOpSeparate(int i, int i1, int i2, int i3) {
      }

      @Override
      public void glTexParameterfv(int i, int i1, FloatBuffer floatBuffer) {
      }

      @Override
      public void glTexParameteri(int i, int i1, int i2) {
      }

      @Override
      public void glTexParameteriv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public void glUniform1f(int i, float v) {
      }

      @Override
      public void glUniform1fv(int i, int i1, FloatBuffer floatBuffer) {
      }

      @Override
      public void glUniform1fv(int i, int i1, float[] floats, int i2) {
      }

      @Override
      public void glUniform1i(int i, int i1) {
      }

      @Override
      public void glUniform1iv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public void glUniform1iv(int i, int i1, int[] ints, int i2) {
      }

      @Override
      public void glUniform2f(int i, float v, float v1) {
      }

      @Override
      public void glUniform2fv(int i, int i1, FloatBuffer floatBuffer) {
      }

      @Override
      public void glUniform2fv(int i, int i1, float[] floats, int i2) {
      }

      @Override
      public void glUniform2i(int i, int i1, int i2) {
      }

      @Override
      public void glUniform2iv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public void glUniform2iv(int i, int i1, int[] ints, int i2) {
      }

      @Override
      public void glUniform3f(int i, float v, float v1, float v2) {
      }

      @Override
      public void glUniform3fv(int i, int i1, FloatBuffer floatBuffer) {
      }

      @Override
      public void glUniform3fv(int i, int i1, float[] floats, int i2) {
      }

      @Override
      public void glUniform3i(int i, int i1, int i2, int i3) {
      }

      @Override
      public void glUniform3iv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public void glUniform3iv(int i, int i1, int[] ints, int i2) {
      }

      @Override
      public void glUniform4f(int i, float v, float v1, float v2, float v3) {
      }

      @Override
      public void glUniform4fv(int i, int i1, FloatBuffer floatBuffer) {
      }

      @Override
      public void glUniform4fv(int i, int i1, float[] floats, int i2) {
      }

      @Override
      public void glUniform4i(int i, int i1, int i2, int i3, int i4) {
      }

      @Override
      public void glUniform4iv(int i, int i1, IntBuffer intBuffer) {
      }

      @Override
      public void glUniform4iv(int i, int i1, int[] ints, int i2) {
      }

      @Override
      public void glUniformMatrix2fv(int i, int i1, boolean b, FloatBuffer floatBuffer) {
      }

      @Override
      public void glUniformMatrix2fv(int i, int i1, boolean b, float[] floats, int i2) {
      }

      @Override
      public void glUniformMatrix3fv(int i, int i1, boolean b, FloatBuffer floatBuffer) {
      }

      @Override
      public void glUniformMatrix3fv(int i, int i1, boolean b, float[] floats, int i2) {
      }

      @Override
      public void glUniformMatrix4fv(int i, int i1, boolean b, FloatBuffer floatBuffer) {
      }

      @Override
      public void glUniformMatrix4fv(int i, int i1, boolean b, float[] floats, int i2) {
      }

      @Override
      public void glUseProgram(int i) {
      }

      @Override
      public void glValidateProgram(int i) {
      }

      @Override
      public void glVertexAttrib1f(int i, float v) {
      }

      @Override
      public void glVertexAttrib1fv(int i, FloatBuffer floatBuffer) {
      }

      @Override
      public void glVertexAttrib2f(int i, float v, float v1) {
      }

      @Override
      public void glVertexAttrib2fv(int i, FloatBuffer floatBuffer) {
      }

      @Override
      public void glVertexAttrib3f(int i, float v, float v1, float v2) {
      }

      @Override
      public void glVertexAttrib3fv(int i, FloatBuffer floatBuffer) {
      }

      @Override
      public void glVertexAttrib4f(int i, float v, float v1, float v2, float v3) {
      }

      @Override
      public void glVertexAttrib4fv(int i, FloatBuffer floatBuffer) {
      }

      @Override
      public void glVertexAttribPointer(int i, int i1, int i2, boolean b, int i3, Buffer buffer) {
      }

      @Override
      public void glVertexAttribPointer(int i, int i1, int i2, boolean b, int i3, int i4) {
      }
    };

    // 2. Your app logic
    ApplicationListener listener = new ApplicationListener() {
      @Override public void create() {
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

      @Override public void resize(int width, int height) {}
      @Override public void render() {}
      @Override public void pause() {}
      @Override public void resume() {}
      @Override public void dispose() {}
    };
    new HeadlessApplication(listener, config);
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      help();
      return;
    }

    if (args[0].equals("g3dj-mod")) {
      if (args.length > 2) {
        pipeline(args);
      }
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