package backend.academy.ui.pickers;

import backend.academy.model.SymmetryType;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.RadioBoxList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Setter;

@Setter
public class SymmetryPicker extends AbstractPicker<SymmetryType> {
    private Optional<SymmetryType> selectedSymmetry;

    public SymmetryPicker() {
        this.selectedSymmetry = Optional.empty();
    }

    @Override
    public Component createComponent() {
        Map<String, SymmetryType> descriptionToTypeMap = new LinkedHashMap<>();
        Arrays.stream(SymmetryType.values()).forEach(symmetryType ->
            descriptionToTypeMap.put(symmetryType.description(), symmetryType)
        );

        RadioBoxList<String> symmetryList = createRadioBoxList(
            descriptionToTypeMap.keySet(),
            null,
            description -> {
                if (description == null) {
                    selectedSymmetry = Optional.empty();
                } else {
                    selectedSymmetry = Optional.of(descriptionToTypeMap.get(description));
                }
            },
            true
        );

        mainPanel.addComponent(symmetryList);

        return addBorder(mainPanel, SYMMETRY_NAME_BORDER);
    }

    @Override
    public SymmetryType getSelected() {
        return selectedSymmetry.orElse(null);
    }
}
