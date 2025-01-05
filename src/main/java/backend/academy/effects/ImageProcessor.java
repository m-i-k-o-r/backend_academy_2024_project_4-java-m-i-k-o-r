package backend.academy.effects;

import backend.academy.image.FractalImage;

@FunctionalInterface
public interface ImageProcessor {
    void process(FractalImage image);

    default String name() {
        return "обычный процесс, тут не на что смотреть...";
    }

    default boolean requiresInput() {
        return false;
    }

    default void setInputValue(double value) {
        throw new UnsupportedOperationException(" ! ОШИБКАААААА");
    }
}
