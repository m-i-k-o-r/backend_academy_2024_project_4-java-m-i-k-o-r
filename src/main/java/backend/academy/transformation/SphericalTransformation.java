package backend.academy.transformation;

import backend.academy.model.Point;

public class SphericalTransformation implements Transformation {
    @Override
    public Point apply(Point p) {
        double r = p.x() * p.x() + p.y() * p.y();
        return new Point(p.x() / r, p.y() / r);
    }

    @Override
    public String name() {
        return "Spherical";
    }
}
