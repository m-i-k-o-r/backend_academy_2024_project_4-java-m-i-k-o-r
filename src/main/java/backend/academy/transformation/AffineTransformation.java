package backend.academy.transformation;

import backend.academy.model.Point;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AffineTransformation implements Transformation {
    private final double a, b, c, d, e, f;

    @Override
    public Point apply(Point point) {
        return new Point(
            a * point.x() + b * point.y() + c,
            d * point.x() + e * point.y() + f
        );
    }
}
