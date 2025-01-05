package backend.academy.ui.pickers;

import backend.academy.transformation.Transformation;
import com.googlecode.lanterna.gui2.CheckBox;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Panel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransformationPicker extends AbstractPicker<List<Transformation>> {
    private final List<Transformation> transformations;
    private final List<Transformation> selectedTransformations;

    private final Map<Transformation, CheckBox> transformationToCheckBox;

    public TransformationPicker(List<Transformation> transformations) {
        this.transformations = transformations;
        this.selectedTransformations = new ArrayList<>();
        this.transformationToCheckBox = new LinkedHashMap<>();
    }

    @Override
    public Component createComponent() {
        Panel panel = createBasePanel(Direction.VERTICAL);

        for (Transformation t : transformations) {
            CheckBox checkBox = createCheckBox(t);
            panel.addComponent(checkBox);
            transformationToCheckBox.put(t, checkBox);
        }

        return addBorder(panel, TRANSFORMATION_NAME_BORDER);
    }

    private CheckBox createCheckBox(Transformation transformation) {
        return createCheckBox(
            getCheckBoxLabel(transformation),
            false,
            checked -> {
                if (checked) {
                    selectedTransformations.add(transformation);
                } else {
                    selectedTransformations.remove(transformation);
                }
                updateCheckBoxLabels();
            }
        );
    }

    private void updateCheckBoxLabels() {
        for (Map.Entry<Transformation, CheckBox> entry : transformationToCheckBox.entrySet()) {
            entry.getValue().setLabel(getCheckBoxLabel(entry.getKey()));
        }
    }

    private String getCheckBoxLabel(Transformation transformation) {
        int index = selectedTransformations.indexOf(transformation);
        return index != -1
            ? "[" + (index + 1) + "] " + transformation.name()
            : "[-] " + transformation.name();
    }

    @Override
    public List<Transformation> getSelected() {
        return new ArrayList<>(selectedTransformations);
    }
}
