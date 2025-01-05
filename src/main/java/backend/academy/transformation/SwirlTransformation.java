package backend.academy.transformation;

import backend.academy.model.Point;

public class SwirlTransformation implements Transformation {
    @Override
    public Point apply(Point p) {
        double rSquared = p.x() * p.x() + p.y() * p.y();

        double newX = p.x() * Math.sin(rSquared) - p.y() * Math.cos(rSquared);
        double newY = p.x() * Math.cos(rSquared) + p.y() * Math.sin(rSquared);

        return new Point(newX, newY);
    }

    @Override
    public String name() {
        return "Swirl";
    }
}
