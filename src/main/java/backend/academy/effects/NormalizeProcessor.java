package backend.academy.effects;

import backend.academy.FractalImage;
import backend.academy.model.Pixel;

public class NormalizeProcessor implements ImageProcessor {
    @Override
    public void process(FractalImage image) {
        double maxHitCount = 0.0;
        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                maxHitCount = Math.max(maxHitCount, image.pixel(x, y).hitCount());
            }
        }

        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                Pixel pixel = image.pixel(x, y);

                int intensity = 0;
                if (maxHitCount > 0) {
                    intensity = (int) (255 * Math.log10(1 + pixel.hitCount()) / Math.log10(1 + maxHitCount));
                }

                Pixel newPixel = new Pixel(
                    pixel.r(),
                    pixel.g(),
                    pixel.b(),
                    intensity
                );
                image.setPixel(x, y, newPixel);
            }
        }
    }
}
