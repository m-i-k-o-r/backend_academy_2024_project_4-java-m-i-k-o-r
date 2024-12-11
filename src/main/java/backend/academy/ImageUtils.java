package backend.academy;

import backend.academy.model.Pixel;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public final class ImageUtils {
    private ImageUtils() {}

    public static void save(FractalImage image, String filename, String format) throws Exception {
        BufferedImage img = new BufferedImage(image.width(), image.height(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                Pixel pixel = image.pixel(x, y);

                Color c = new Color(pixel.r(), pixel.g(), pixel.b(), pixel.hitCount());
                img.setRGB(x, y, c.getRGB());
            }
        }

        ImageIO.write(img, format, new File(filename));
    }
}
