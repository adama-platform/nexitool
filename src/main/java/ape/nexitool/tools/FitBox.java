package ape.nexitool.tools;

import ape.nexitool.tools.json.IsStaticMesh;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class FitBox {
  public static void process(String inputPath, String outputPath, double size) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode root = (ObjectNode) mapper.readTree(new File(inputPath));
    Sizer.Dimensions dimensions = new Sizer.Dimensions(root);
    double scale_x = size / ( dimensions.max_x - dimensions.min_x );
    double scale_z = size / ( dimensions.max_z - dimensions.min_z );
    IsStaticMesh.warnIfNotStaticMesh(root);
    Resize.processPostLoad(root, outputPath, Math.min(scale_x, scale_z));
  }
}
