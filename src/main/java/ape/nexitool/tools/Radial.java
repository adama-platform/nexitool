package ape.nexitool.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Radial {
  // Stage 2[algorithm] Picking a Cluster (assumes candidates come from yx scanning such that any new (x, y) is after any prior point in the cluster
  private static PreciseAccumulator pickBest(ArrayList<PreciseAccumulator> prior, Candidate candidate) {
    for (PreciseAccumulator p : prior) {
      if (p.isTouching(candidate)) {
        return p;
      }
    }
    return null;
  }

  // Stage2[algorithm] Assemble the clusters from pixel space
  private static ArrayList<PreciseAccumulator> estimateEdges(ArrayList<Candidate> candidates) {
    ArrayList<PreciseAccumulator> result = new ArrayList<PreciseAccumulator>();
    for (Candidate candidate : candidates) {
      if (result.isEmpty()) {
        PreciseAccumulator first = new PreciseAccumulator();
        first.add(candidate);
        result.add(first);
        continue;
      }
      PreciseAccumulator winner = pickBest(result, candidate);
      if (winner == null) {
        winner = new PreciseAccumulator();
        result.add(winner);
      }
      winner.add(candidate);
    }
    return result;
  }

  // Stage 3[algorithm] polarize all the points
  private static ArrayList<RadialPoint> polarize(double cx, double cy, ArrayList<PreciseAccumulator> points) {
    ArrayList<RadialPoint> result = new ArrayList<>();
    for (PreciseAccumulator point : points) {
      result.add(new RadialPoint(point.x() - cx, point.y() - cy));
    }
    return result;
  }

  // Stage 4+:[algorithm] dumb way to check colinear by thinking in threshold degrees
  private static double measure(double ax, double ay, double bx, double by) {
    double da = Math.atan2(ay, ax);
    double db = Math.atan2(by, bx);
    double diff = Math.abs(da - db) % (2 * Math.PI);
    return Math.min(diff, 2 * Math.PI - diff) * 180 / Math.PI;
  }

  // Stage 4+[algorithm] reduce the set by throwing away co-linear points.
  private static ArrayList<RadialPoint> reduce(ArrayList<RadialPoint> points, double threshold) {
    points.sort(RadialPoint::compareTo);
    ArrayList<RadialPoint> reduced = new ArrayList<RadialPoint>();
    RadialPoint last = points.get(0);
    reduced.add(last);
    for (int k = 1; k < points.size(); k++) {
      RadialPoint current = points.get(k);
      RadialPoint next = points.get((k + 1) % points.size());
      if (measure(
              current.dx - last.dx,
              current.dy - last.dy,
              next.dx - last.dx,
              next.dy - last.dy) >= threshold) {
        reduced.add(current);
        last = current;
      }
    }
    return reduced;
  }

  public static double radiusIntersect(double angle, RadialPoint a, RadialPoint b) {
    double start = a.angle;
    double end = b.angle;
    double lambda = 1.0 - (angle - start) / (end - start);
    return a.radius * lambda + b.radius * (1 - lambda);
  }

  public static ArrayList<RadialPoint> superSample(ArrayList<RadialPoint> points, int divisions) {
    ArrayList<RadialPoint> result = new ArrayList<>();
    Sampler sampler = new Sampler(points);
    double step = 2 * Math.PI / divisions;
    for (double angle = 0.0; angle < 2 * Math.PI; angle += step) {
      double radius = sampler.radius(angle);
      result.add(new RadialPoint(radius * Math.cos(angle), radius * Math.sin(angle)));
    }
    return result;
  }

  public static ArrayList<RadialPoint> averageWithFlipY(ArrayList<RadialPoint> points, int divisions) {
    ArrayList<RadialPoint> flipped = new ArrayList<>();
    for (RadialPoint rp : points) {
      flipped.add(new RadialPoint(-rp.dx, rp.dy));
    }
    points.sort(RadialPoint::compareTo);
    flipped.sort(RadialPoint::compareTo);
    ArrayList<RadialPoint> result = new ArrayList<>();
    Sampler sampler_a = new Sampler(points);
    Sampler sampler_b = new Sampler(flipped);
    double step = 2 * Math.PI / divisions;
    for (double angle = 0.0; angle < 2 * Math.PI; angle += step) {
      double radius = (sampler_a.radius(angle) + sampler_b.radius(angle)) / 2;
      result.add(new RadialPoint(radius * Math.cos(angle), radius * Math.sin(angle)));
    }
    return result;
  }

  private static ArrayList<RadialPoint> killExactlyOne(ArrayList<RadialPoint> points) {
    points.sort(RadialPoint::compareTo);
    RadialPoint winner = null;
    double winnerScore = 1000000;
    RadialPoint last = points.get(0);
    for (int k = 1; k < points.size(); k++) {
      RadialPoint current = points.get(k);
      RadialPoint next = points.get((k + 1) % points.size());
      double candidate = (current.dx - last.dx) * (next.dx - last.dx) + (current.dy - last.dy) * (next.dy - last.dy);
      if (candidate < winnerScore) {
        winner = current;
        winnerScore = candidate;
      }
      last = current;
    }
    if (winner != null) {
      winner.kill();
    }
    ArrayList<RadialPoint> reduced = new ArrayList<RadialPoint>();
    for (RadialPoint rp : points) {
      if (rp.alive()) {
        reduced.add(rp);
      }
    }
    if (winner != null) {
      winner.resurrect();
    }
    return reduced;
  }

  private static ArrayList<RadialPoint> flipY(ArrayList<RadialPoint> points) {
    ArrayList<RadialPoint> reduced = new ArrayList<RadialPoint>();
    for (RadialPoint rp : points) {
      if (rp.dx >= 0) {
        reduced.add(new RadialPoint(rp.dx, rp.dy));
        reduced.add(new RadialPoint(-rp.dx, rp.dy));
      }
    }
    reduced.sort(RadialPoint::compareTo);
    return reduced;
  }

  public static void process(String input, String output) throws Exception {
    BufferedImage img = ImageIO.read(new File(input));
    ArrayList<Candidate> edges = new ArrayList<Candidate>();
    PreciseAccumulator center = new PreciseAccumulator();
    for (int x = 0; x < img.getWidth(); x++) {
      for (int y = 0; y < img.getHeight(); y++) {
        Color c = new Color(img.getRGB(x, y));
        if (c.getRed() > 224 && c.getGreen() < 24 && c.getBlue() < 24) {
          edges.add(new Candidate(x, y));
        }
        if (c.getRed() < 24 && c.getGreen() < 24 && c.getBlue() > 224) {
          center.add(new Candidate(x, y));
        }
      }
    }
    BufferedImage test = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < img.getWidth(); x++) {
      for (int y = 0; y < img.getHeight(); y++) {
        test.setRGB(x, y, Color.WHITE.getRGB());
      }
    }
    ArrayList<PreciseAccumulator> reduced = estimateEdges(edges);
    double cx = center.x();
    double cy = center.y();
    ArrayList<RadialPoint> polar = polarize(cx, cy, reduced);
    polar.sort(RadialPoint::compareTo);
    polar = reduce(polar, 1);
    polar = averageWithFlipY(polar, 100);
    polar = reduce(polar, 1);
    while (polar.size() > 64) {
      polar = killExactlyOne(polar);
    }
    System.out.println(polar.size());
    polar = flipY(polar);
    while (polar.size() > 64) {
      polar = killExactlyOne(polar);
    }
    double max_radius = 1;
    for (RadialPoint p : polar) {
      int x = (int) (cx - Math.cos(p.angle) * p.radius);
      int y = (int) (cy + Math.sin(p.angle) * p.radius);
      test.setRGB(x, y, Color.RED.getRGB());
      max_radius = Math.max(max_radius, p.radius);
    }
    StringBuilder func = new StringBuilder();
    func.append("function radial() -> float[] {\n  return [");
    boolean notFirst = false;
    for (RadialPoint p : polar) {
      if (notFirst) {
        func.append(", ");
      }
      notFirst = true;
      func.append(p.angle + "f, " + (p.radius / max_radius)).append("f\n      ");
    }
    func.append("];\n}\n");
    System.out.println(func);
    test.setRGB((int) cx, (int) cy, Color.RED.getRGB());
    if (output != null) {
      ImageIO.write(test, "png", new File(output));
    }
  }

  // Stage 1: Coordinates in Pixel Space
  private static class Candidate {
    public final int x;
    public final int y;

    public Candidate(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  // Stage 2[representation]: A Cluster Points in Pixel Space
  private static class PreciseAccumulator {
    public ArrayList<Candidate> candidates;
    public int n;
    private double x;
    private double y;
    private Candidate last;

    public PreciseAccumulator() {
      this.x = 0;
      this.y = 0;
      this.n = 0;
      this.candidates = new ArrayList<Candidate>();
    }

    private static boolean isNeighbor(Candidate c1, Candidate c2) {
      return Math.abs(c1.x - c2.x) <= 2 && Math.abs(c1.y - c2.y) <= 2;
    }

    public void add(Candidate candidate) {
      this.x += candidate.x;
      this.y += candidate.y;
      this.n++;
      this.candidates.add(candidate);
    }

    public double x() {
      return x / n;
    }

    public double y() {
      return y / n;
    }

    public boolean isTouching(Candidate candidate) {
      if (last != null) {
        if (isNeighbor(last, candidate)) {
          last = candidate;
          return true;
        }
      }
      for (Candidate c : candidates) {
        if (isNeighbor(c, candidate)) {
          last = candidate;
          return true;
        }
      }
      return false;
    }
  }

  // Stage 3[representation]: represent the points around the center
  private static class RadialPoint implements Comparable<RadialPoint> {
    public final double dx;
    public final double dy;
    public final double radius;
    public final double angle;
    private boolean killed;

    public RadialPoint(double dx, double dy) {
      this.dx = dx;
      this.dy = dy;
      this.radius = Math.sqrt(dx * dx + dy * dy);
      double raw = Math.atan2(dy, dx);
      if (raw < 0) {
        raw += Math.PI * 2;
      }
      this.angle = raw;
      this.killed = false;
    }

    public RadialPoint(double dx, double dy, double radius, double angle) {
      this.dx = dx;
      this.dy = dy;
      this.radius = radius;
      this.angle = angle;
      this.killed = false;
    }

    public void kill() {
      this.killed = true;
    }

    public void resurrect() {
      this.killed = false;
    }

    public boolean alive() {
      return !killed;
    }

    @Override
    public int compareTo(RadialPoint o) {
      return Double.compare(angle, o.angle);
    }

    public RadialPoint augment(int m) {
      return new RadialPoint(dx, dy, radius, angle + 2 * Math.PI * m);
    }
  }

  public static class Sampler {
    private final ArrayList<RadialPoint> points;
    private final Iterator<RadialPoint> it;
    private RadialPoint last;
    private RadialPoint current;
    private boolean done;

    public Sampler(ArrayList<RadialPoint> points) {
      this.points = points;
      this.it = points.iterator();
      this.last = points.get(points.size() - 1).augment(-1);
      this.current = it.next();
      this.done = false;
    }

    public double radius(double angle) {
      if (!done) {
        while (angle > current.angle && !done) {
          if (it.hasNext()) {
            last = current;
            current = it.next();
          } else {
            last = current;
            current = points.get(0).augment(1);
            done = true;
          }
        }
      }
      return radiusIntersect(angle, last, current);
    }
  }
}