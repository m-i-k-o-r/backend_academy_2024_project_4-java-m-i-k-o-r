package backend.academy.ui;

import backend.academy.effects.ImageProcessor;
import backend.academy.model.ImageFormat;
import backend.academy.model.SymmetryType;
import backend.academy.transformation.Transformation;
import backend.academy.ui.pickers.AbstractPicker;
import backend.academy.ui.pickers.BackgroundColorPicker;
import backend.academy.ui.pickers.ImageFormatPicker;
import backend.academy.ui.pickers.PaletteColorPicker;
import backend.academy.ui.pickers.ParameterPicker;
import backend.academy.ui.pickers.ProcessorPicker;
import backend.academy.ui.pickers.SymmetryPicker;
import backend.academy.ui.pickers.ThreadPicker;
import backend.academy.ui.pickers.TransformationPicker;
import backend.academy.util.FractalConfig;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import java.awt.Color;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UIComponentFactory {
    public static AbstractPicker<ParameterPicker.Parameters> createParameterPicker() {
        return new ParameterPicker(
            FractalConfig.DEFAULT_WIDTH,
            FractalConfig.DEFAULT_HEIGHT,
            FractalConfig.DEFAULT_SAMPLES,
            FractalConfig.DEFAULT_ITERATIONS
        );
    }

    public static AbstractPicker<ImageFormat> createImageFormatPicker() {
        return new ImageFormatPicker(
            FractalConfig.DEFAULT_FORMAT
        );
    }

    public static AbstractPicker<SymmetryType> createSymmetryPicker() {
        return new SymmetryPicker();
    }

    public static AbstractPicker<List<Transformation>> createVariationsPicker() {
        return new TransformationPicker(
            FractalConfig.DEFAULT_TRANSFORMATIONS
        );
    }

    public static AbstractPicker<List<ImageProcessor>> createProcessorPicker(WindowBasedTextGUI textGUI) {
        return new ProcessorPicker(
            textGUI,
            FractalConfig.DEFAULT_PROCESSORS
        );
    }

    public static AbstractPicker<Color> createBackgroundColorPicker() {
        return new BackgroundColorPicker(
            FractalConfig.DEFAULT_BACKGROUND_COLORS
        );
    }

    public static AbstractPicker<List<Color>> createPaletteColorPicker() {
        return new PaletteColorPicker(
            FractalConfig.DEFAULT_PALETTE_COLORS
        );
    }

    public static AbstractPicker<ThreadPicker.Type> createThreadPicker() {
        return new ThreadPicker();
    }
}
