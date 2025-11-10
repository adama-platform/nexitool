package ape.nexitool.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class FitBox {
  public static void process(String inputPath, String outputPath, double size) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    Sizer.Dimensions dimensions = new Sizer.Dimensions(root);
    double scale_x = size / ( dimensions.max_x - dimensions.min_x );
    double scale_z = size / ( dimensions.max_z - dimensions.min_z );
    Resize.processPostLoad(root, outputPath, Math.min(scale_x, scale_z));
  }
}
