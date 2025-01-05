package backend.academy;

import backend.academy.model.FractalGenerationContext;
import backend.academy.ui.UIComponentFactory;
import backend.academy.ui.pickers.AbstractPicker;
import backend.academy.ui.pickers.BackgroundColorPicker;
import backend.academy.ui.pickers.ImageFormatPicker;
import backend.academy.ui.pickers.PaletteColorPicker;
import backend.academy.ui.pickers.ParameterPicker;
import backend.academy.ui.pickers.ProcessorPicker;
import backend.academy.ui.pickers.SymmetryPicker;
import backend.academy.ui.pickers.ThreadPicker;
import backend.academy.ui.pickers.TransformationPicker;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static backend.academy.util.FractalConfig.DEFAULT_SIZE_WINDOW;
import static com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger.CloseOnEscape;

/**
 * Основой класс приложения
 * <br>
 * Отвечает за инициализацию пользовательского интерфейса (GUI),
 * управление основным окном и связку компонентов для создания и настройки фракталов
 */
public class FractalApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(FractalApp.class);

    private final WindowBasedTextGUI textGUI;
    private final FractalService fractalService;
    private final Map<PickerType, AbstractPicker<?>> pickers;

    public FractalApp() {
        this(initializeGUI());
    }

    public FractalApp(WindowBasedTextGUI textGUI) {
        this.textGUI = textGUI;
        this.fractalService = new FractalService();
        this.pickers = createPickers();
    }

    /**
     * Инициализация пользовательского интерфейса
     *
     * @return объект {@link WindowBasedTextGUI}.
     * @throws IllegalStateException если не удалось инициализировать графический интерфейс
     */
    private static WindowBasedTextGUI initializeGUI() {
        try {
            SwingTerminalFrame terminal = new SwingTerminalFrame(CloseOnEscape);
            terminal.setPreferredSize(new Dimension(DEFAULT_SIZE_WINDOW, DEFAULT_SIZE_WINDOW));
            terminal.setVisible(true);

            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            return new MultiWindowTextGUI(screen);
        } catch (Exception e) {
            String errorMessage = "Не удалось инициализировать графический интерфейс";
            LOGGER.error(errorMessage, e);
            throw new IllegalStateException(errorMessage, e);
        }
    }

    /**
     * Запускает основное окно приложения
     */
    public void start() {
        try {
            Window mainWindow = createMainWindow();
            textGUI.addWindowAndWait(mainWindow);
        } catch (Exception e) {
            LOGGER.error("Ошибка при запуске приложения: ", e);
        }
    }

    /**
     * Создает набор компонентов выбора (пикеров) для параметров генерации
     *
     * @return карта пикеров {@link Map} (ключ это {@link PickerType})
     */
    private Map<PickerType, AbstractPicker<?>> createPickers() {
        return Map.of(
            PickerType.PARAMETER,       UIComponentFactory.createParameterPicker(),
            PickerType.FORMAT,          UIComponentFactory.createImageFormatPicker(),
            PickerType.SYMMETRY,        UIComponentFactory.createSymmetryPicker(),
            PickerType.THREAD,          UIComponentFactory.createThreadPicker(),
            PickerType.TRANSFORMATION,  UIComponentFactory.createVariationsPicker(),
            PickerType.PROCESSOR,       UIComponentFactory.createProcessorPicker(textGUI),
            PickerType.BACKGROUND,      UIComponentFactory.createBackgroundColorPicker(),
            PickerType.PALETTE,         UIComponentFactory.createPaletteColorPicker()
        );
    }

    /**
     * Создает основную панель приложения
     *
     * @return {@link Panel} интерфейс (панель) с компонентами для настройки фрактала
     */
    private Panel createMainPanel() {
        Panel mainPanel = new Panel(new GridLayout(2));

        Panel leftColumn = new Panel(new GridLayout(1));
        leftColumn.addComponent(pickers.get(PickerType.PARAMETER).createComponent(),
            GridLayout.createHorizontallyFilledLayoutData());
        leftColumn.addComponent(pickers.get(PickerType.FORMAT).createComponent(),
            GridLayout.createHorizontallyFilledLayoutData());
        leftColumn.addComponent(pickers.get(PickerType.THREAD).createComponent(),
            GridLayout.createHorizontallyFilledLayoutData());

        Panel rightColumn = new Panel(new GridLayout(1));
        rightColumn.addComponent(pickers.get(PickerType.SYMMETRY).createComponent(),
            GridLayout.createHorizontallyFilledLayoutData());
        rightColumn.addComponent(pickers.get(PickerType.TRANSFORMATION).createComponent(),
            GridLayout.createHorizontallyFilledLayoutData());
        rightColumn.addComponent(pickers.get(PickerType.PROCESSOR).createComponent(),
            GridLayout.createHorizontallyFilledLayoutData());
        rightColumn.addComponent(pickers.get(PickerType.BACKGROUND).createComponent(),
            GridLayout.createHorizontallyFilledLayoutData());
        rightColumn.addComponent(pickers.get(PickerType.PALETTE).createComponent(),
            GridLayout.createHorizontallyFilledLayoutData());

        rightColumn.addComponent(createGenerateButton(),
            GridLayout.createHorizontallyFilledLayoutData());

        mainPanel.addComponent(leftColumn);
        mainPanel.addComponent(rightColumn);

        return mainPanel;
    }

    /**
     * Создает кнопку для генерации фрактала
     *
     * @return {@link Component} кнопка
     */
    private Component createGenerateButton() {
        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Button generateButton = new Button("Сгенерировать", () -> {
            try {
                fractalService.generate(
                    FractalGenerationContext.builder()
                        .parameterPicker((ParameterPicker) pickers.get(PickerType.PARAMETER))
                        .formatPicker((ImageFormatPicker) pickers.get(PickerType.FORMAT))
                        .symmetryPicker((SymmetryPicker) pickers.get(PickerType.SYMMETRY))
                        .threadPicker((ThreadPicker) pickers.get(PickerType.THREAD))
                        .variationsPicker((TransformationPicker) pickers.get(PickerType.TRANSFORMATION))
                        .processorPicker((ProcessorPicker) pickers.get(PickerType.PROCESSOR))
                        .backgroundColorPicker((BackgroundColorPicker) pickers.get(PickerType.BACKGROUND))
                        .paletteColorPicker((PaletteColorPicker) pickers.get(PickerType.PALETTE))
                        .build(),
                    textGUI
                );
            } catch (Exception e) {
                LOGGER.error("Ошибка при генерации фрактала: ", e);
                showErrorDialog(e.getMessage());
            }
        });

        buttonPanel.addComponent(generateButton);
        return buttonPanel;
    }

    /**
     * Создает главное окно приложения
     *
     * @return {@link Window} главное окно
     */
    private Window createMainWindow() {
        Window window = new BasicWindow("Фрактальное Пламя");
        window.setHints(List.of(Window.Hint.MENU_POPUP));

        Panel mainPanel = createMainPanel();
        window.setComponent(mainPanel);

        return window;
    }

    /**
     * Показывает диалоговое окно с сообщением об ошибке
     *
     * @param message текст сообщения об ошибке
     */
    private void showErrorDialog(String message) {
        new MessageDialogBuilder()
            .setTitle("Упс...")
            .setText("Произошла ошибка: " + message)
            .build()
            .showDialog(textGUI);
    }

    /**
     * Перечисление типов пикеров для параметров генерации
     */
    private enum PickerType {
        PARAMETER,
        FORMAT,
        SYMMETRY,
        THREAD,
        TRANSFORMATION,
        PROCESSOR,
        BACKGROUND,
        PALETTE
    }
}
