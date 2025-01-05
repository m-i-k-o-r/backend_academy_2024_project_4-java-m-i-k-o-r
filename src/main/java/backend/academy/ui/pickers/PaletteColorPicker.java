package backend.academy.ui.pickers;

import com.googlecode.lanterna.gui2.CheckBox;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Panel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaletteColorPicker extends AbstractPicker<List<Color>> {
    private final Map<Color, String> colors;
    private final List<Color> selectedColors;

    public PaletteColorPicker(Map<Color, String> colors) {
        this.colors = colors;
        this.selectedColors = new ArrayList<>();
    }

    @Override
    public Component createComponent() {
        Panel panel = createBasePanel(Direction.VERTICAL);

        colors.forEach((color, label) -> {
            CheckBox checkBox = createCheckBox(
                label,
                false,
                isChecked -> {
                    if (isChecked) {
                        selectedColors.add(color);
                    } else {
                        selectedColors.remove(color);
                    }
                }
            );
            panel.addComponent(checkBox);
        });

        return addBorder(panel, PALETTE_NAME_BORDER);
    }

    @Override
    public List<Color> getSelected() {
        return selectedColors;
    }
}
