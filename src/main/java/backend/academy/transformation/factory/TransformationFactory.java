package backend.academy.transformation.factory;

import backend.academy.transformation.Transformation;
import backend.academy.transformation.wrapper.Wrapper;
import java.util.List;

public interface TransformationFactory<T extends Transformation> {
    List<Wrapper<T>> wrap(List<T> transformations);
}
