package backend.academy.transformation;

import backend.academy.model.Point;

public class HeartTransformation implements Transformation {
    @Override
    public Point apply(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        double theta = Math.atan2(p.y(), p.x());

        double newX = r * Math.sin(theta * r);
        double newY = -r * Math.cos(theta * r);

        return new Point(newX, newY);
    }

    @Override
    public String name() {
        return "Heart";
    }
}
