package crdt;

import java.util.List;
import java.util.UUID;

public class Stroke {
    private final String id;
    private final List<Point> points;
    private final String color;
    private final double thickness;
    private final long timestamp;
    private final String author;


    public Stroke(List<Point> points, String color, double thickness, long timestamp, String author) {
        this.id = UUID.randomUUID().toString();
        this.points = points;
        this.color = color;
        this.thickness = thickness;
        this.timestamp = timestamp;
        this.author = author;
    }


    public String getId() { return id; }
    public List<Point> getPoints() { return points; }
    public String getColor() { return color; }
    public double getThickness() { return thickness; }
    public long getTimestamp() { return timestamp; }
    public String getAuthor() { return author; }


    public static class Point {
        private final double x;
        private final double y;
        public Point() { this.x = 0; this.y = 0; }
        public Point(double x, double y) { this.x = x; this.y = y; }
        public double getX() { return x; }
        public double getY() { return y; }
    }
}
