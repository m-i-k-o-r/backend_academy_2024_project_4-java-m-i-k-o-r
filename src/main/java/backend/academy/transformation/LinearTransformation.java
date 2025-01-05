package backend.academy.transformation;

import backend.academy.model.Point;

public class LinearTransformation implements Transformation {
    @Override
    public Point apply(Point p) {
        return new Point(p.x(), p.y());
    }

    @Override
    public String name() {
        return "Linear";
    }
}
