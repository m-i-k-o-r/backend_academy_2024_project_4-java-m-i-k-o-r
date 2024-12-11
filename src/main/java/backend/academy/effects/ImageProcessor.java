package backend.academy.effects;

import backend.academy.FractalImage;

@FunctionalInterface
public interface ImageProcessor {
    void process(FractalImage image);
}
