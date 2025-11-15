package net.msymbios.rlovelyr.client.gui.control.controls.panels;

import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.properties.enums.GridDefinition;
import net.msymbios.rlovelyr.client.gui.properties.enums.Visibility;

import java.util.*;

/**
 * A panel that arranges its children in a grid.
 */
public class GridPanel extends Control {
    
    private final List<Definition> rowDefinitions = new ArrayList<>();

    private final List<Definition> columnDefinitions = new ArrayList<>();

    private final Map<BaseControl, GridCell> cells = new HashMap<>();

    private double[] rowHeights = new double[0];

    private double[] columnWidths = new double[0];

    @Override
    public void measureChildren() {
        double availableWidth = (getDesiredWidth() - getPaddingWidth()) / getInnerScale().x;
        double availableHeight = (getDesiredHeight() - getPaddingHeight()) / getInnerScale().y;

        rowHeights = createRowHeights();
        columnWidths = createColumnWidths();

        populateFixedRowHeights(rowHeights);
        populateFixedColumnWidths(columnWidths);

        // Measure all the fixed rows and subtract their height from the available height.
        availableHeight = measureFixedRows(availableHeight);

        // Measure all the fixed columns and subtract their width from the available width.
        availableWidth = measureFixedColumns(availableWidth);

        // Measure all the auto rows with fixed sized children and subtract their height from the available height.
        availableHeight = measureAutoRowsWithFixedSizedChildren(availableHeight);

        // Measure all the auto columns with fixed sized children and subtract their width from the available width.
        availableWidth = measureAutoColumnsWithFixedSizedChildren(availableWidth);

        // Measure all the auto rows with percentage sized children.
        measureAutoRowsWithPercentageSizedChildren(availableHeight);

        // Measure all the auto columns with percentage sized children.
        measureAutoColumnsWithPercentageSizedChildren(availableWidth);

        // Measure all the ratio rows and columns.
        measureRatioCells(availableWidth, availableHeight);
    }

    private void measureRatioCells(double availableWidth, double availableHeight) {
        double totalRowRatio = rowDefinitions.stream().filter(d -> d.definition == GridDefinition.RATIO).mapToDouble(d -> d.value).sum();
        double totalColumnRatio = columnDefinitions.stream().filter(d -> d.definition == GridDefinition.RATIO).mapToDouble(d -> d.value).sum();

        for (int row = 0; row < rowHeights.length; row++) {
            GridDefinition rowDefinition = getRowDefinition(row);

            if (rowDefinition != GridDefinition.RATIO) {
                continue;
            }

            double rowHeight = availableHeight * (rowDefinitions.get(row).value / totalRowRatio);

            for (BaseControl controlInRow : getRowControls(row)) {
                if (controlInRow.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                controlInRow.doMeasure(-1.0, rowHeight - controlInRow.getMarginHeight());
            }

            rowHeights[row] = rowHeight;
        }

        for (int col = 0; col < columnWidths.length; col++) {
            GridDefinition columnDefinition = getColumnDefinition(col);

            if (columnDefinition != GridDefinition.RATIO) {
                continue;
            }

            double columnWidth = availableWidth * (columnDefinitions.get(col).value / totalColumnRatio);

            for (BaseControl controlInColumn : getColumnControls(col)) {
                if (controlInColumn.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                controlInColumn.doMeasure(columnWidth - controlInColumn.getMarginWidth(), -1.0);
            }

            columnWidths[col] = columnWidth;
        }
    }

    private void measureAutoColumnsWithPercentageSizedChildren(double availableWidth) {
        for (int col = 0; col < columnWidths.length; col++) {
            GridDefinition columnDefinition = getColumnDefinition(col);

            if (columnDefinition != GridDefinition.AUTO) {
                continue;
            }

            for (BaseControl controlInColumn : getColumnControls(col)) {
                if (controlInColumn.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                if (controlInColumn.getWidthPercentage() == null) {
                    continue;
                }

                double columnWidth = columnWidths[col];
                columnWidth = columnWidth == -1.0 ? availableWidth : columnWidth;

                controlInColumn.doMeasure(columnWidth - controlInColumn.getMarginWidth(), -1.0);
            }
        }
    }

    private void measureAutoRowsWithPercentageSizedChildren(double availableHeight) {
        for (int row = 0; row < rowHeights.length; row++) {
            GridDefinition rowDefinition = getRowDefinition(row);

            if (rowDefinition != GridDefinition.AUTO) {
                continue;
            }

            for (BaseControl controlInRow : getRowControls(row)) {
                if (controlInRow.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                if (controlInRow.getHeightPercentage() == null) {
                    continue;
                }

                double rowHeight = rowHeights[row];
                rowHeight = rowHeight == -1.0 ? availableHeight : rowHeight;

                controlInRow.doMeasure(-1.0, rowHeight - controlInRow.getMarginHeight());
            }
        }
    }

    private double measureAutoColumnsWithFixedSizedChildren(double availableWidth) {
        for (int col = 0; col < columnWidths.length; col++) {
            GridDefinition columnDefinition = getColumnDefinition(col);

            if (columnDefinition != GridDefinition.AUTO) {
                continue;
            }

            double maxColumnWidth = -1.0;

            for (BaseControl controlInColumn : getColumnControls(col)) {
                if (controlInColumn.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                if (controlInColumn.getWidthPercentage() != null) {
                    continue;
                }

                controlInColumn.doMeasure(availableWidth - controlInColumn.getMarginWidth(), -1.0);

                double desiredWidth = controlInColumn.getDesiredWidth() + controlInColumn.getMarginWidth();

                if (desiredWidth > maxColumnWidth) {
                    maxColumnWidth = desiredWidth;
                }
            }

            availableWidth -= maxColumnWidth == -1.0 ? 0.0 : maxColumnWidth;
            availableWidth = Math.max(0.0, availableWidth);

            if (maxColumnWidth > columnWidths[col]) {
                columnWidths[col] = maxColumnWidth;
            }
        }

        return availableWidth;
    }

    private double measureAutoRowsWithFixedSizedChildren(double availableHeight) {
        for (int row = 0; row < rowHeights.length; row++) {
            GridDefinition rowDefinition = getRowDefinition(row);

            if (rowDefinition != GridDefinition.AUTO) {
                continue;
            }

            double maxRowHeight = -1.0;

            for (BaseControl controlInRow : getRowControls(row)) {
                if (controlInRow.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                if (controlInRow.getHeightPercentage() != null) {
                    continue;
                }

                controlInRow.doMeasure(-1.0, availableHeight - controlInRow.getMarginHeight());

                double desiredHeight = controlInRow.getDesiredHeight() + controlInRow.getMarginHeight();

                if (desiredHeight > maxRowHeight) {
                    maxRowHeight = desiredHeight;
                }
            }

            availableHeight -= maxRowHeight == -1.0 ? 0.0 : maxRowHeight;
            availableHeight = Math.max(0.0, availableHeight);

            if (maxRowHeight > rowHeights[row]) {
                rowHeights[row] = maxRowHeight;
            }
        }

        return availableHeight;
    }

    private double measureFixedColumns(double availableWidth) {
        for (int col = 0; col < columnWidths.length; col++) {
            GridDefinition columnDefinition = getColumnDefinition(col);

            if (columnDefinition != GridDefinition.FIXED) {
                continue;
            }

            double columnWidth = columnWidths[col];

            for (BaseControl controlInColumn : getColumnControls(col)) {
                if (controlInColumn.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                controlInColumn.doMeasure(columnWidth - controlInColumn.getMarginWidth(), -1.0);
            }

            availableWidth -= columnWidth;
        }

        return availableWidth;
    }

    private double measureFixedRows(double availableHeight) {
        for (int row = 0; row < rowHeights.length; row++) {
            GridDefinition rowDefinition = getRowDefinition(row);

            if (rowDefinition != GridDefinition.FIXED) {
                continue;
            }

            double rowHeight = rowHeights[row];

            for (BaseControl controlInRow : getRowControls(row)) {
                if (controlInRow.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                controlInRow.doMeasure(-1.0, rowHeight - controlInRow.getMarginHeight());
            }

            availableHeight -= rowHeight;
        }

        return availableHeight;
    }

    private double[] createRowHeights() {
        double[] rowHeights = new double[rowDefinitions.size()];

        for (int i = 0; i < rowHeights.length; i++) {
            rowHeights[i] = -1.0;
        }

        return rowHeights;
    }

    private double[] createColumnWidths() {
        double[] columnWidths = new double[columnDefinitions.size()];

        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = -1.0;
        }

        return columnWidths;
    }

    private void populateFixedRowHeights(double[] rowHeights) {
        int i = 0;
        for (Definition definition : rowDefinitions) {
            if (definition.definition == GridDefinition.FIXED) {
                rowHeights[i] = definition.value;
            }

            i++;
        }
    }

    private void populateFixedColumnWidths(double[] columnWidths) {
        int i = 0;
        for (Definition definition : columnDefinitions) {
            if (definition.definition == GridDefinition.FIXED) {
                columnWidths[i] = definition.value;
            }

            i++;
        }
    }

    @Override
    public void arrange() {
        for (int row = 0; row < rowHeights.length; row++) {
            for (int col = 0; col < columnWidths.length; col++) {
                double x = col == 0 ? 0.0 : Arrays.stream(columnWidths).limit(col).sum();
                double y = row == 0 ? 0.0 : Arrays.stream(rowHeights).limit(row).sum();
                double width = columnWidths[col];
                double height = rowHeights[row];

                for (BaseControl control : getCellControls(row, col)) {
                    if (control.getVisibility() == Visibility.COLLAPSED) {
                        continue;
                    }

                    control.setWidth(control.getDesiredWidth());
                    control.setHeight(control.getDesiredHeight());

                    control.setX(x + (width - control.getWidthWithMargin()) * getHorizontalAlignmentFor(control) + control.getMargin().left);
                    control.setY(y + (height - control.getHeightWithMargin()) * getVerticalAlignmentFor(control) + control.getMargin().top);
                }
            }
        }
    }

    private List<BaseControl> getCellControls(int row, int column) {
        List<BaseControl> controls = new ArrayList<>();

        for (BaseControl control : cells.keySet()) {
            GridCell cell = cells.get(control);
            if (cell.row == row && cell.column == column) {
                controls.add(control);
            }
        }

        return controls;
    }

    private List<BaseControl> getRowControls(int row) {
        List<BaseControl> controls = new ArrayList<>();

        for (BaseControl control : cells.keySet()) {
            GridCell cell = cells.get(control);
            if (cell.row == row) {
                controls.add(control);
            }
        }

        return controls;
    }

    private List<BaseControl> getColumnControls(int column) {
        List<BaseControl> controls = new ArrayList<>();

        for (BaseControl control : cells.keySet()) {
            GridCell cell = cells.get(control);
            if (cell.column == column) {
                controls.add(control);
            }
        }

        return controls;
    }

    private GridDefinition getRowDefinition(int row) {
        int i = 0;
        for (Definition definition : rowDefinitions) {
            if (i == row) {
                return definition.definition;
            }

            i++;
        }

        return GridDefinition.AUTO;
    }

    private GridDefinition getColumnDefinition(int column) {
        int i = 0;
        for (Definition definition : columnDefinitions) {
            if (i == column) {
                return definition.definition;
            }

            i++;
        }

        return GridDefinition.AUTO;
    }

    @Override
    public void addChild(BaseControl child) {
        cells.put(child, new GridCell(0, 0));

        super.addChild(child);
    }

    public void addChild(BaseControl child, int row, int column) {
        cells.put(child, new GridCell(row, column));

        super.addChild(child);
    }

    @Override
    public void removeChild(BaseControl child) {
        removeChild(child, false);
    }

    @Override
    public void removeChild(BaseControl child, boolean preserveEventSubscribers) {
        cells.remove(child);

        super.removeChild(child, preserveEventSubscribers);
    }

    /**
     * Adds a row definition to the grid with an optional value.
     *
     * @param definition the definition.
     * @param value      the value.
     */
    public void addRowDefinition(GridDefinition definition, double value) {
        rowDefinitions.add(new Definition(definition, value));
    }

    /**
     * Adds a column definition to the grid with an optional value.
     *
     * @param definition the definition.
     * @param value      the value.
     */
    public void addColumnDefinition(GridDefinition definition, double value) {
        columnDefinitions.add(new Definition(definition, value));
    }

    private static class Definition {
        private final GridDefinition definition;
        private final double value;

        /**
         * @param definition the definition.
         * @param value      the value.
         */
        public Definition(GridDefinition definition, double value) {
            this.definition = definition;
            this.value = value;
        }
    }

    private static class GridCell {
        private final int row;
        private final int column;

        /**
         * @param row    the row.
         * @param column the column.
         */
        public GridCell(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }
}
