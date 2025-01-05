package backend.academy.saver;

import backend.academy.image.FractalImage;
import backend.academy.model.Pixel;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import static backend.academy.util.ColorUtils.blendColors;

public abstract class ImageSaver {
    private static final int CHUNK_SIZE = 100;
    private final int threadCount;

    public ImageSaver(int threadCount) {
        this.threadCount = threadCount <= 0 ? Runtime.getRuntime().availableProcessors() : threadCount;
    }

    public void save(FractalImage image, Color backgroundColor, String filename) throws IOException {
        BufferedImage bufferedImage = createBufferedImage(image.width(), image.height());

        Color provenColor = resolveBackgroundColor(backgroundColor);
        processImageInParallel(image, provenColor, bufferedImage);

        saveImage(bufferedImage, filename);
    }

    protected abstract BufferedImage createBufferedImage(int width, int height);

    protected abstract void saveImage(BufferedImage image, String filename) throws IOException;

    protected abstract Color resolveBackgroundColor(Color backgroundColor);

    private void processImageInParallel(FractalImage fractalImage, Color backgroundColor, BufferedImage bufferedImage) {
        try (ForkJoinPool pool = new ForkJoinPool(threadCount)) {
            pool.invoke(new ProcessImageTask(fractalImage, backgroundColor, bufferedImage, 0, fractalImage.height()));
        }
    }

    private void processChunk(
        FractalImage fractalImage,
        Color backgroundColor,
        BufferedImage bufferedImage,
        int startX,
        int startY,
        int endX,
        int endY
    ) {
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                Pixel pixel = fractalImage.pixel(x, y);
                bufferedImage.setRGB(x, y, pixel.hitCount() > 0
                    ? blendColors(backgroundColor, pixel.color()).getRGB()
                    : backgroundColor.getRGB());
            }
        }
    }

    private class ProcessImageTask extends RecursiveAction {
        private final FractalImage fractalImage;
        private final Color backgroundColor;
        private final BufferedImage bufferedImage;
        private final int startY;
        private final int endY;

        private ProcessImageTask(
            FractalImage fractalImage,
            Color backgroundColor,
            BufferedImage bufferedImage,
            int startY,
            int endY
        ) {
            this.fractalImage = fractalImage;
            this.backgroundColor = backgroundColor;
            this.bufferedImage = bufferedImage;
            this.startY = startY;
            this.endY = endY;
        }

        @Override
        protected void compute() {
            if (endY - startY <= CHUNK_SIZE) {
                processChunk(fractalImage, backgroundColor, bufferedImage, 0, startY, fractalImage.width(), endY);
            } else {
                int midY = startY + (endY - startY) / 2;
                invokeAll(
                    new ProcessImageTask(fractalImage, backgroundColor, bufferedImage, startY, midY),
                    new ProcessImageTask(fractalImage, backgroundColor, bufferedImage, midY, endY)
                );
            }
        }
    }
}
