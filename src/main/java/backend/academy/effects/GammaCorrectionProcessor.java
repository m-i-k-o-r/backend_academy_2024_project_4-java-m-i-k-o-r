package backend.academy.effects;

import backend.academy.image.FractalImage;
import backend.academy.model.Pixel;
import lombok.Getter;
import static backend.academy.util.ColorUtils.COLOR_MAX_VALUE;
import static backend.academy.util.FractalConfig.DEFAULT_GAMMA_VALUE;

@Getter
public class GammaCorrectionProcessor implements ImageProcessor {
    private double gamma;

    public GammaCorrectionProcessor(double gamma) {
        this.gamma = validateGamma(gamma);
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

    @Override
    public String name() {
        return "Gamma Correction";
    }

    @Override
    public boolean requiresInput() {
        return true;
    }

    @Override
    public void setInputValue(double value) {
        this.gamma = validateGamma(value);
        if (gamma <= 0) {
            throw new IllegalArgumentException("Гамма не может быть меньше 0");
        }
    }

    private static double validateGamma(double gamma) {
        return gamma > 0
            ? gamma
            : DEFAULT_GAMMA_VALUE;
    }

    private int calculateGamma(int value) {
        return (int) (Math.pow(value / (double) COLOR_MAX_VALUE, gamma) * COLOR_MAX_VALUE);
    }
}
