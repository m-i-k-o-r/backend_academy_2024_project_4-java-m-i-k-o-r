package backend.academy.effects;

import backend.academy.image.FractalImage;
import backend.academy.model.Pixel;
import static backend.academy.util.ColorUtils.COLOR_MAX_VALUE;

public class InvertColorsProcessor implements ImageProcessor {
    @Override
    public void process(FractalImage image) {
        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                Pixel original = image.pixel(x, y);
                Pixel updatedPixel = new Pixel(
                    COLOR_MAX_VALUE - original.r(),
                    COLOR_MAX_VALUE - original.g(),
                    COLOR_MAX_VALUE - original.b(),
                    original.hitCount()
                );
                image.setPixel(x, y, updatedPixel);
            }
        }
    }

    @Override
    public String name() {
        return "Invert Colors";
    }
}
