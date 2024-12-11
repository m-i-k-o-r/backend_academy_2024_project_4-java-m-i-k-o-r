package backend.academy.effects;

import backend.academy.FractalImage;
import backend.academy.model.Pixel;
import java.awt.Color;

public class InvertColorsProcessor implements ImageProcessor {
    @Override
    public void process(FractalImage image) {
        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                Pixel original = image.pixel(x, y);
                Pixel updatedPixel = new Pixel(
                    (255 - original.r()),
                    (255 - original.g()),
                    (255 - original.b()),
                    original.hitCount()
                );
                image.setPixel(x, y, updatedPixel);
            }
        }
    }
}
