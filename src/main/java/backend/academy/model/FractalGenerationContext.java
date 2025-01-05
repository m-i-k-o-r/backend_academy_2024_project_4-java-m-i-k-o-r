package backend.academy.model;

import backend.academy.effects.ImageProcessor;
import backend.academy.transformation.Transformation;
import backend.academy.ui.pickers.AbstractPicker;
import backend.academy.ui.pickers.ParameterPicker;
import backend.academy.ui.pickers.ThreadPicker;
import java.awt.Color;
import java.util.List;
import lombok.Builder;

@Builder
public final class FractalGenerationContext {
    private final AbstractPicker<ParameterPicker.Parameters> parameterPicker;
    private final AbstractPicker<ImageFormat> formatPicker;
    private final AbstractPicker<SymmetryType> symmetryPicker;
    private final AbstractPicker<ThreadPicker.Type> threadPicker;
    private final AbstractPicker<List<Transformation>> variationsPicker;
    private final AbstractPicker<List<ImageProcessor>> processorPicker;
    private final AbstractPicker<Color> backgroundColorPicker;
    private final AbstractPicker<List<Color>> paletteColorPicker;

    private <T> T getSelected(AbstractPicker<T> picker) {
        return picker.getSelected();
    }

    public ParameterPicker.Parameters parameters() {
        return getSelected(parameterPicker);
    }

    public ImageFormat format() {
        return getSelected(formatPicker);
    }

    public SymmetryType symmetry() {
        return getSelected(symmetryPicker);
    }

    public ThreadPicker.Type thread() {
        return getSelected(threadPicker);
    }

    public List<Transformation> variations() {
        return getSelected(variationsPicker);
    }

    public List<ImageProcessor> processors() {
        return getSelected(processorPicker);
    }

    public Color backgroundColor() {
        return getSelected(backgroundColorPicker);
    }

    public List<Color> paletteColors() {
        return getSelected(paletteColorPicker);
    }
}
