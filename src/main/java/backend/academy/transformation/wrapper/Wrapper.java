package backend.academy.transformation.wrapper;

import backend.academy.transformation.Transformation;
import java.awt.Color;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Wrapper<T extends Transformation> {
    private final T transformation;

    @Setter
    private double weight;

    private final Color color;

    public Wrapper(T transformation, double weight, Color color) {
        this.color = color;
        this.weight = weight;
        this.transformation = transformation;
    }

    public Wrapper(T transformation, double weight) {
        this.transformation = transformation;
        this.weight = weight;
        this.color = null;
    }
}
