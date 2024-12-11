package backend.academy.transformation;

import backend.academy.model.Point;

public class ExponentialTransformation implements Transformation {
    @Override
    public Point apply(Point p) {
        double factor = Math.exp(p.x() - 1);

        return new Point(
            factor * Math.cos(Math.PI * p.y()),
            factor * Math.sin(Math.PI * p.y())
        );
    }
}
