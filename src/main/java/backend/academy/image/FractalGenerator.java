package backend.academy.image;

import backend.academy.model.Rect;
import backend.academy.transformation.AffineTransformation;
import backend.academy.transformation.Transformation;
import backend.academy.transformation.wrapper.Wrapper;
import java.util.List;

public interface FractalGenerator {
    FractalImage generate(
        FractalImage canvas,
        Rect world,
        List<Wrapper<AffineTransformation>> affine,
        List<Wrapper<Transformation>> variations,
        int samples,
        int iterPerSample,
        long seed
    );
}
