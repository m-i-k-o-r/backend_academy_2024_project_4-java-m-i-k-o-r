package backend.academy.ui.pickers;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.RadioBoxList;
import com.googlecode.lanterna.gui2.TextBox;
import java.util.List;
import lombok.Setter;

@Setter
public class ThreadPicker extends AbstractPicker<ThreadPicker.Type> {
    private boolean isMultithreading;
    private int threadCount;

    public ThreadPicker() {
        this.isMultithreading = false;
        this.threadCount = 0;
    }

    @Override
    public Component createComponent() {
        Panel panel = createBasePanel(Direction.VERTICAL);

        RadioBoxList<Boolean> boolList = createRadioBoxList(
            List.of(Boolean.TRUE, Boolean.FALSE),
            isMultithreading,
            selected -> {
                isMultithreading = selected;
                updateVisibility(panel);
            },
            false
        );
        panel.addComponent(boolList);

        Label threadCountLabel = new Label("Кол-во потоков:");
        panel.addComponent(threadCountLabel);

        TextBox threadCountTextBox = createValidatedTextBox(
            threadCount,
            POSITIVE_INTEGER,
            newText -> {
                try {
                    this.threadCount = Integer.parseInt(newText);
                } catch (NumberFormatException ignored) {

                }
            }
        );
        panel.addComponent(threadCountTextBox);

        Label bottomLabel = new Label("0 - не ограниченное кол-во");
        panel.addComponent(bottomLabel);

        updateVisibility(panel);

        return addBorder(panel, THREAD_NAME_BORDER);
    }

    private static final String THREAD_LABEL_TEXT = "потоки/не потоки:";

    private void updateVisibility(Panel panel) {
        panel.getChildren().forEach(component -> {
            if (component instanceof Label label) {
                label.setVisible(isMultithreading || THREAD_LABEL_TEXT.equals(label.getText()));
            }
            if (component instanceof TextBox textBox) {
                textBox.setVisible(isMultithreading);
            }
        });
    }

    @Override
    public Type getSelected() {
        return new Type(
            isMultithreading,
            isMultithreading
                ? threadCount
                : 0
        );
    }

    public record Type(
        boolean isMultithreading,
        int threadCount
    ) {

    }
}
