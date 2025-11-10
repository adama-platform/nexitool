package ape.nexitool.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class SetHeight {
  public static void process(String inputPath, String outputPath, double height) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(inputPath));
    Sizer.Dimensions dimensions = new Sizer.Dimensions(root);
    double scale = height / ( dimensions.max_y - dimensions.min_y );
    Resize.processPostLoad(root, outputPath, scale);
  }
}
