package backend.academy.ui.pickers;

import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.CheckBox;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.RadioBoxList;
import com.googlecode.lanterna.gui2.TextBox;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public abstract class AbstractPicker<T> {
    protected static final Pattern POSITIVE_INTEGER = Pattern.compile("\\d*");
    protected static final Pattern POSITIVE_DOUBLE = Pattern.compile("\\d+(\\.\\d+)?|");
    protected static final Pattern INTEGER_OR_EMPTY = Pattern.compile("-?\\d*");

    protected static final String PARAMETER_NAME_BORDER = "Параметры изображения";
    protected static final String FORMAT_NAME_BORDER = "Формат вывода";
    protected static final String SYMMETRY_NAME_BORDER = "Симметрия";
    protected static final String THREAD_NAME_BORDER = "Использование многопоточности";
    protected static final String TRANSFORMATION_NAME_BORDER = "Преобразования";
    protected static final String PROCESSOR_NAME_BORDER = "Post обработчики";
    protected static final String BACKGROUND_NAME_BORDER = "Цвет фона";
    protected static final String PALETTE_NAME_BORDER = "Цвета палитры";

    protected final Panel mainPanel;

    protected AbstractPicker() {
        this.mainPanel = new Panel();
        initializeMainPanel();
    }

    private void initializeMainPanel() {
        this.mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
    }

    protected Panel createBasePanel(Direction direction) {
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(direction));
        return panel;
    }

    protected TextBox createValidatedTextBox(
        Object defaultValue,
        Pattern validationPattern,
        Consumer<String> updateAction
    ) {
        return new TextBox(defaultValue != null ? String.valueOf(defaultValue) : "")
            .setValidationPattern(validationPattern)
            .setTextChangeListener((newText, change) -> {
                if (!newText.isEmpty()) {
                    try {
                        updateAction.accept(newText);
                    } catch (NumberFormatException ignored) {
                        // Ignore invalid input
                    }
                }
            });
    }

    protected Panel createLabeledComponent(String labelText, Component component) {
        Panel panel = createBasePanel(Direction.VERTICAL);
        panel.addComponent(new Label(labelText));
        panel.addComponent(component);
        return panel;
    }

    protected <E> RadioBoxList<E> createRadioBoxList(
        Iterable<E> items,
        E defaultSelected,
        Consumer<E> onSelectChanged,
        boolean mayBeUnSelect
    ) {
        RadioBoxList<E> radioBoxList = new RadioBoxList<>();
        items.forEach(radioBoxList::addItem);
        radioBoxList.setCheckedItem(defaultSelected);

        radioBoxList.addListener((previousIndex, currentIndex) -> {
            if (mayBeUnSelect && currentIndex == previousIndex) {
                radioBoxList.setCheckedItem(null);
                onSelectChanged.accept(null);
            } else {
                E selectedItem = radioBoxList.getCheckedItem();
                onSelectChanged.accept(selectedItem);
            }
        });

        return radioBoxList;
    }


    protected CheckBox createCheckBox(
        String label,
        boolean defaultChecked,
        Consumer<Boolean> onCheckChanged
    ) {
        CheckBox checkBox = new CheckBox(label);
        checkBox.setChecked(defaultChecked);
        checkBox.addListener(onCheckChanged::accept);
        return checkBox;
    }

    protected Component addBorder(Component component, String title) {
        return component.withBorder(Borders.singleLine(title));
    }

    protected EmptySpace createEmptySpace() {
        return new EmptySpace();
    }

    public abstract Component createComponent();

    public abstract T getSelected();
}
