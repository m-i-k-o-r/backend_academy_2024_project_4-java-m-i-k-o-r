package backend.academy.ui.pickers;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.RadioBoxList;
import java.awt.Color;
import java.util.Map;
import java.util.Optional;

public class BackgroundColorPicker extends AbstractPicker<Color> {
    private final Map<Color, String> colors;
    private Color selectedColor;

    public BackgroundColorPicker(Map<Color, String> colors) {
        this.colors = colors;
        this.selectedColor = null;
    }

    @Override
    public Component createComponent() {
        Panel panel = createBasePanel(Direction.VERTICAL);

        RadioBoxList<String> colorToRadioBox = createRadioBoxList(
            colors.values(),
            null,
            selectedLabel -> {
                if (selectedLabel == null) {
                    selectedColor = null;
                } else {
                    selectedColor = getColorByLabel(selectedLabel)
                        .orElseThrow(() -> new IllegalStateException("Неверный выбор цвета"));
                }
            },
            true
        );

        panel.addComponent(colorToRadioBox);
        return addBorder(panel, BACKGROUND_NAME_BORDER);
    }

    @Override
    public Color getSelected() {
        return selectedColor;
    }

    private Optional<Color> getColorByLabel(String label) {
        return colors.entrySet().stream()
            .filter(entry -> entry.getValue().equals(label))
            .map(Map.Entry::getKey)
            .findFirst();
    }
}
