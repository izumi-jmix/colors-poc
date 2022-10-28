package org.izumi.jmix.colorspoc.screen.colors;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.HtmlAttributes;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.MarginInfo;
import io.jmix.ui.component.ScrollBoxLayout;
import io.jmix.ui.component.VBoxLayout;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

/**
  * @author Aiden Izumi (aka Flamesson).
 */
@UiController("ColorsScreen")
@UiDescriptor("colors-screen.xml")
public class ColorsScreen extends Screen {
    private static final int MAX_COLORS_COLUMN_SIZE = 3;
    private static final Collection<Color> AVAILABLE_COLORS = List.of(
            Color.RED, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.CYAN, Color.GREEN, Color.MAGENTA
    );

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private HtmlAttributes htmlAttributes;

    @Autowired
    private Label<String> selectedColorLabel;
    @Autowired
    private ScrollBoxLayout colorsContainer;

    private final Map<Color, Component> map = new HashMap<>();
    private Color selected;

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        setAvailableColors(AVAILABLE_COLORS);
    }

    public void setAvailableColors(Collection<Color> availableColors) {
        reset();
        updateColorsGrid(availableColors);
    }

    public void select(Color color) {
        if (!map.containsKey(color)) {
            throw new IllegalArgumentException("Given color is not registered!");
        }

        selected = color;
        selectedColorLabel.setValue(color.toString());
    }

    public Optional<Color> getSelected() {
        return Optional.ofNullable(selected);
    }

    private void reset() {
        colorsContainer.removeAll();
        map.clear();
        selected = null;
        selectedColorLabel.clear();
    }

    private void updateColorsGrid(Collection<Color> colors) {
        final var columnSize = MAX_COLORS_COLUMN_SIZE;
        final var filledColumns = colors.size() / columnSize;
        final var lastColumnSize = colors.size() % columnSize;

        for (int i = 0; i < filledColumns; i++) {
            final var column = toColumn(colors.stream()
                    .skip(i * columnSize)
                    .limit(columnSize)
                    .collect(Collectors.toList()));
            colorsContainer.add(column);
        }

        if (lastColumnSize != 0) {
            final var lastColumn = toColumn(colors.stream()
                    .skip(filledColumns * columnSize)
                    .limit(lastColumnSize)
                    .collect(Collectors.toList()));
            colorsContainer.add(lastColumn);
        }
    }

    private VBoxLayout toColumn(Collection<Color> colors) {
        final VBoxLayout column = uiComponents.create(VBoxLayout.NAME);
        column.setSpacing(true);
        column.setMargin(new MarginInfo(true, true, true, false));

        colors.forEach(color -> {
            final var component = toComponent(color);
            column.add(component);
            map.put(color, column);
        });

        return column;
    }

    private Component toComponent(Color color) {
        final VBoxLayout layout = uiComponents.create(VBoxLayout.NAME);
        htmlAttributes.applyCss(layout, buildCss(color));
        layout.addLayoutClickListener(event -> select(color));
        return layout;
    }

    private String buildCss(Color color) {
        return "background-color: rgb(" +
                color.getRed() +
                ", " +
                color.getGreen() +
                ", " +
                color.getBlue() +
                "); width: 16px; height: 16px";
    }
}