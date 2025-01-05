package backend.academy.transformation;

import backend.academy.model.Point;

public class ExTransformation implements Transformation {
    public static final int CUB = 3;

    @Override
    public Point apply(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        double theta = Math.atan2(p.y(), p.x());

        double p0 = Math.sin(theta + r);
        double p1 = Math.cos(theta - r);

        double newX = r * (Math.pow(p0, CUB) + Math.pow(p1, CUB));
        double newY = r * (Math.pow(p0, CUB) - Math.pow(p1, CUB));

        return new Point(newX, newY);
    }

    @Override
    public String name() {
        return "Ex";
    }
}
