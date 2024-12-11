package backend.academy;

import backend.academy.model.Pixel;
import backend.academy.model.Point;
import backend.academy.model.Rect;
import backend.academy.transformation.AffineTransformation;
import backend.academy.transformation.Transformation;
import backend.academy.transformation.wrapper.Wrapper;
import backend.academy.util.WrapperUtils;
import java.awt.Color;
import java.util.List;
import java.util.Random;

public class FractalRenderer {
    public FractalImage render(
        FractalImage canvas,
        Rect world,
        List<Wrapper<AffineTransformation>> affine,
        List<Wrapper<Transformation>> variations,
        int samples,
        int iterPerSample,
        long seed
    ) {
        Random random = new Random(seed);

        for (int i = 0; i < samples; i++) {
            Point point = world.randomPoint(random);

            for (int j = 0; j < iterPerSample; j++) {
                Wrapper<AffineTransformation> a = WrapperUtils.choose(random, affine);
                point = a.transformation().apply(point);

                Color transformColor = a.color();

                Wrapper<Transformation> variation = WrapperUtils.choose(random, variations);
                point = variation.transformation().apply(point);

                if (world.contains(point)) {
                    int x = (int) ((point.x() - world.x()) / world.width() * canvas.width());
                    int y = (int) ((point.y() - world.y()) / world.height() * canvas.height());
                    if (canvas.contains(x, y)) {
                        Pixel currentPixel = canvas.pixel(x, y);
                        Pixel updatedPixel;

                        if (currentPixel.hitCount() == 0) {
                            updatedPixel = new Pixel(
                                transformColor.getRed(),
                                transformColor.getGreen(),
                                transformColor.getBlue(),
                                1
                            );
                        } else {
                            updatedPixel = new Pixel(
                                Math.min(255, (currentPixel.r() + transformColor.getRed()) / 2),
                                Math.min(255, (currentPixel.g() + transformColor.getGreen()) / 2),
                                Math.min(255, (currentPixel.b() + transformColor.getBlue()) / 2),
                                currentPixel.hitCount() + 1
                            );
                        }

                        canvas.setPixel(x, y, updatedPixel);
                    }
                }
            }
        }
        return canvas;
    }
}
