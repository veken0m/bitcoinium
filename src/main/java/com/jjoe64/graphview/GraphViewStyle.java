package com.jjoe64.graphview;

import android.graphics.Color;

/**
 * Styles for the GraphView Important: Use GraphViewSeries.GraphViewSeriesStyle
 * for series-specify styling
 */
public class GraphViewStyle {
    private int vLabelsColor;
    private int hLabelsColor;
    private int gridColor;

    public GraphViewStyle() {
        vLabelsColor = Color.BLACK;
        hLabelsColor = Color.BLACK;
        gridColor = Color.DKGRAY;
    }

    public GraphViewStyle(int vLabelsColor, int hLabelsColor, int gridColor) {
        this.vLabelsColor = vLabelsColor;
        this.hLabelsColor = hLabelsColor;
        this.gridColor = gridColor;
    }

    public int getVerticalLabelsColor() {
        return vLabelsColor;
    }

    public void setVerticalLabelsColor(int c) {
        vLabelsColor = c;
    }

    public int getHorizontalLabelsColor() {
        return hLabelsColor;
    }

    public void setHorizontalLabelsColor(int c) {
        hLabelsColor = c;
    }

    public int getGridColor() {
        return gridColor;
    }

    public void setGridColor(int c) {
        gridColor = c;
    }
}
