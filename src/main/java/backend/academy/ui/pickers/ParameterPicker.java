package backend.academy.ui.pickers;

import com.googlecode.lanterna.gui2.Component;
import java.security.SecureRandom;
import lombok.Setter;

@Setter
public class ParameterPicker extends AbstractPicker<ParameterPicker.Parameters> {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private int width;
    private int height;
    private int samples;
    private int iterations;
    private String seedInput = "";

    public ParameterPicker(
        int defaultWidth,
        int defaultHeight,
        int defaultSamples,
        int defaultIterations
    ) {
        this.width = defaultWidth;
        this.height = defaultHeight;
        this.samples = defaultSamples;
        this.iterations = defaultIterations;
    }

    @Override
    public Component createComponent() {
        mainPanel.addComponent(createLabeledComponent("Ширина:",
            createValidatedTextBox(
                width,
                POSITIVE_INTEGER,
                newText -> this.width = Integer.parseInt(newText)
            )));

        mainPanel.addComponent(createLabeledComponent("Высота:",
            createValidatedTextBox(
                height,
                POSITIVE_INTEGER,
                newText -> this.height = Integer.parseInt(newText)
            )));

        mainPanel.addComponent(createLabeledComponent("Количество выборок (samples):",
            createValidatedTextBox(
                samples,
                POSITIVE_INTEGER,
                newText -> this.samples = Integer.parseInt(newText)
            )));

        mainPanel.addComponent(createLabeledComponent("Количество итераций:",
            createValidatedTextBox(
                iterations,
                POSITIVE_INTEGER,
                newText -> this.iterations = Integer.parseInt(newText)
            )));

        mainPanel.addComponent(createLabeledComponent("Seed (опционально):",
            createValidatedTextBox(
                seedInput,
                INTEGER_OR_EMPTY,
                newText -> this.seedInput = newText
            )));

        return addBorder(mainPanel, PARAMETER_NAME_BORDER);
    }

    @Override
    public Parameters getSelected() {
        long finalSeed = seedInput.isEmpty()
            ? SECURE_RANDOM.nextLong()
            : Long.parseLong(seedInput);

        return new Parameters(
            width,
            height,
            samples,
            iterations,
            finalSeed
        );
    }

    public record Parameters(
        int width,
        int height,
        int samples,
        int iterations,
        long seed
    ) {

    }
}
