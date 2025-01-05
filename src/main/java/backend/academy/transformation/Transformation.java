package backend.academy.transformation;

import backend.academy.model.Point;
import java.util.Random;
import java.util.function.Function;

public interface Transformation extends Function<Point, Point> {
    default String name() {
        return "";
    }

    default void random(Random random) {

    }
}
