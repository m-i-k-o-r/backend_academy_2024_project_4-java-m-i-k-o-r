package backend.academy.ui.pickers;

import backend.academy.model.ImageFormat;
import com.googlecode.lanterna.gui2.Component;
import java.util.Arrays;

public class ImageFormatPicker extends AbstractPicker<ImageFormat> {
    private ImageFormat selectedFormat;

    public ImageFormatPicker(ImageFormat defaultFormat) {
        this.selectedFormat = defaultFormat;
    }

    @Override
    public Component createComponent() {
        mainPanel.addComponent(createRadioBoxList(
            Arrays.asList(ImageFormat.values()),
            selectedFormat,
            newFormat -> this.selectedFormat = newFormat,
            false
        ));

        return addBorder(mainPanel, FORMAT_NAME_BORDER);
    }

    @Override
    public ImageFormat getSelected() {
        return selectedFormat;
    }
}
