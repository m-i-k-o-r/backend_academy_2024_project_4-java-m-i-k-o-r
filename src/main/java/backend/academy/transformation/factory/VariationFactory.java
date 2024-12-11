package backend.academy.transformation.factory;

import backend.academy.transformation.Transformation;
import backend.academy.transformation.wrapper.Wrapper;
import backend.academy.util.WrapperUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VariationFactory implements TransformationFactory<Transformation> {
    private final Random random;

    public VariationFactory(Random random) {
        this.random = random;
    }

    @Override
    public List<Wrapper<Transformation>> wrap(List<Transformation> transformations) {
        List<Wrapper<Transformation>> wrappers = new ArrayList<>();
        for (Transformation transformation : transformations) {
            double weight = random.nextDouble();
            wrappers.add(new Wrapper<>(transformation, weight, null));
        }
        return WrapperUtils.normalize(wrappers);
    }
}
