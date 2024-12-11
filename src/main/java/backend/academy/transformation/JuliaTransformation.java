package backend.academy.transformation;

import backend.academy.model.Point;
import lombok.AllArgsConstructor;
import java.util.Random;

@AllArgsConstructor
public class JuliaTransformation implements Transformation {
    private final Random random;

    @Override
    public Point apply(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        double theta = Math.atan2(p.y(), p.x());
        double omega = random.nextBoolean() ? 0 : Math.PI;

        return new Point(
            Math.sqrt(r) * Math.cos(theta / 2 + omega),
            Math.sqrt(r) * Math.sin(theta / 2 + omega)
        );
    }
}
