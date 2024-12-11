package backend.academy.effects;

import backend.academy.FractalImage;
import backend.academy.model.Pixel;

public class GammaCorrectionProcessor implements ImageProcessor {
    private final double gamma;

    public GammaCorrectionProcessor(double gamma) {
        if (gamma <= 0) {
            throw new IllegalArgumentException(" ! Гамма не может быть меньше 0");
        }
        this.gamma = gamma;
    }

    @Override
    public void process(FractalImage image) {
        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                Pixel original = image.pixel(x, y);
                Pixel updatedPixel = new Pixel(
                    calculateGamma(original.r()),
                    calculateGamma(original.g()),
                    calculateGamma(original.b()),
                    original.hitCount()
                );
                image.setPixel(x, y, updatedPixel);
            }
        }
    }

    private int calculateGamma(int value) {
        return (int) (Math.pow(value / 255.0, gamma) * 255);
    }
}
