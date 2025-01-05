package backend.academy.ui.pickers;

import backend.academy.effects.GammaCorrectionProcessor;
import backend.academy.effects.ImageProcessor;
import backend.academy.effects.LogarithmicGammaProcessor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.CheckBox;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProcessorPicker extends AbstractPicker<List<ImageProcessor>> {
    private final WindowBasedTextGUI textGUI;

    private final List<ImageProcessor> processors;
    private final List<ImageProcessor> selectedProcessors;

    private final Map<ImageProcessor, CheckBox> processorToCheckBox;

    public ProcessorPicker(WindowBasedTextGUI textGUI, List<ImageProcessor> processors) {
        this.textGUI = textGUI;
        this.processors = processors;
        this.selectedProcessors = new ArrayList<>();
        this.processorToCheckBox = new LinkedHashMap<>();
    }

    @Override
    public Component createComponent() {
        Panel panel = createBasePanel(Direction.VERTICAL);

        processors.forEach(processor -> {
            CheckBox checkBox = createCheckBox(
                getCheckBoxLabel(processor),
                false,
                checked -> {
                    if (checked) {
                        selectedProcessors.add(processor);
                        if (processor.requiresInput()) {
                            showInputWindow(processor);
                        }
                    } else {
                        selectedProcessors.remove(processor);
                    }
                }
            );

            processorToCheckBox.put(processor, checkBox);
            panel.addComponent(checkBox);
        });

        return addBorder(panel, PROCESSOR_NAME_BORDER);
    }

    private void showInputWindow(ImageProcessor processor) {
        BasicWindow inputWindow = new BasicWindow("Ввод параметра");
        Panel inputPanel = createBasePanel(Direction.VERTICAL);

        inputPanel.addComponent(new Label("Введите значение для " + processor.name() + ":"));

        TextBox inputField = createValidatedTextBox(
            getCurrentProcessorValue(processor),
            POSITIVE_DOUBLE,
            newText -> {
                try {
                    double value = Double.parseDouble(newText);
                    processor.setInputValue(value);
                    processorToCheckBox.get(processor).setLabel(getCheckBoxLabel(processor));
                } catch (NumberFormatException ignored) {
                    // Ignore invalid input
                }
            }
        );

        inputPanel.addComponent(inputField);

        Button okButton = new Button("OK", inputWindow::close);
        inputPanel.addComponent(okButton);

        inputWindow.setComponent(inputPanel);
        textGUI.addWindowAndWait(inputWindow);
    }

    private String getCheckBoxLabel(ImageProcessor processor) {
        return processor.requiresInput()
            ? processor.name() + " [value = " + getCurrentProcessorValue(processor) + "]"
            : processor.name();
    }

    private String getCurrentProcessorValue(ImageProcessor processor) {
        return switch (processor) {
            case GammaCorrectionProcessor p -> Double.toString(p.gamma());
            case LogarithmicGammaProcessor p -> Double.toString(p.alpha());
            default -> "";
        };
    }

    @Override
    public List<ImageProcessor> getSelected() {
        return selectedProcessors;
    }
}
