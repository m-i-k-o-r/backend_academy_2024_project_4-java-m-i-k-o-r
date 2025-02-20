package backend.academy.model;

import java.util.Random;

public record Rect(double x, double y, double width, double height) {
    public boolean contains(Point p) {
        return p.x() >= x && p.x() <= x + width && p.y() >= y && p.y() <= y + height;
    }

    public Point randomPoint(Random random) {
        return new Point(
            this.x + random.nextDouble() * width,
            this.y + random.nextDouble() * height
        );
    }
}
