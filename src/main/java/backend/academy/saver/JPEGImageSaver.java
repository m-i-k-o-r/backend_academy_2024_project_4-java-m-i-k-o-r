package backend.academy.saver;

import backend.academy.util.PathUtils;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JPEGImageSaver extends ImageSaver {
    public JPEGImageSaver(int threadCount) {
        super(threadCount);
    }

    @Override
    protected BufferedImage createBufferedImage(int width, int height) {
        return new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_RGB
        );
    }

    @Override
    protected void saveImage(BufferedImage image, String filename) throws IOException {
        File outputFile = PathUtils.createSafeFile(filename);
        if (!ImageIO.write(image, "jpeg", outputFile)) {
            throw new IOException("Ошибка при сохранении JPEG: " + filename);
        }
    }

    @Override
    protected Color resolveBackgroundColor(Color backgroundColor) {
        return backgroundColor != null
            ? backgroundColor
            : Color.BLACK;
    }
}
