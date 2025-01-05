package backend.academy.effects;

import backend.academy.image.FractalImage;
import backend.academy.model.Pixel;
import lombok.Getter;
import static backend.academy.util.ColorUtils.COLOR_MAX_VALUE;
import static backend.academy.util.FractalConfig.DEFAULT_ALPHA_VALUE;

@Getter
public class LogarithmicGammaProcessor implements ImageProcessor {
    private double alpha;

    public LogarithmicGammaProcessor(double alpha) {
        this.alpha = validateAlpha(alpha);
    }

    @Override
    public void process(FractalImage image) {
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                Pixel original = image.pixel(x, y);
                Pixel updatedPixel = new Pixel(
                    calculateLogGamma(original.r()),
                    calculateLogGamma(original.g()),
                    calculateLogGamma(original.b()),
                    original.hitCount()
                );
                image.setPixel(x, y, updatedPixel);
            }
        }
    }

    @Override
    public String name() {
        return "Log Gamma Correction";
    }

    @Override
    public boolean requiresInput() {
        return true;
    }

    @Override
    public void setInputValue(double value) {
        this.alpha = validateAlpha(value);
        if (alpha <= 0) {
            throw new IllegalArgumentException("Альфа не может быть меньше 0");
        }
    }

    private static double validateAlpha(double alpha) {
        return alpha > 0
            ? alpha
            : DEFAULT_ALPHA_VALUE;
    }

    private int calculateLogGamma(int value) {
        double normalizedValue = value / (double) COLOR_MAX_VALUE;
        double correctedValue = Math.log(1 + alpha * normalizedValue) / Math.log(1 + alpha);
        return (int) Math.round(correctedValue * COLOR_MAX_VALUE);
    }
}
