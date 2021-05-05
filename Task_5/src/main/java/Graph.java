import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.*;

import static java.lang.Math.log;

public class Graph {
    private SortedMap<Double, Double> relativeFrequency;
    private final int count;
    private final double start;
    private final double end;
    private final XYChart chartFunction;
    private final XYChart chartPolygon;

    public Graph(SortedMap<Double, Double> relativeFrequency, int count) {
        this.chartFunction = new XYChartBuilder().width(750).height(550).title("Эмпирическая функция распределения")
                .xAxisTitle("X").yAxisTitle("F(x)").build();
        chartFunction.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        this.chartPolygon = new XYChartBuilder().width(650).height(350).title("Полигон частотей")
                .xAxisTitle("X").yAxisTitle("p*").build();
        chartPolygon.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        this.relativeFrequency = relativeFrequency;
        this.count = count;
        double startValue = relativeFrequency.firstKey();
        double endValue = relativeFrequency.lastKey();
        double h = (endValue - startValue) * 0.2;
        startValue -= h;
        endValue += h;
        this.start = startValue;
        this.end = endValue;
    }

    public void drawFunction() {
        chartFunction.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Step);
        ArrayList<Double> dataX = new ArrayList<>(), data_Y = new ArrayList<>(), dataY = new ArrayList<>();
        dataX.add(start); dataY.add(0.0);
        relativeFrequency.forEach((number, frequency) -> {
            dataX.add(number);
            data_Y.add(frequency);
        });
        double sum = 0;
        dataY.add(0.0);
        for (int i = 0; i < data_Y.size(); i++) {
            sum += data_Y.get(i);
            dataY.add(sum);
        }
        XYSeries series = chartFunction.addSeries("Эмпирическая функция распределения", dataX, dataY);
        series.setMarker(SeriesMarkers.DIAMOND);
    }

    public void drawPolygonOfFrequencies() {
        chartPolygon.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        ArrayList<Double> dataX = new ArrayList<>(), dataY = new ArrayList<>();
        relativeFrequency.forEach((number, frequency) -> {
            dataX.add(number);
            dataY.add(frequency);
        });
        XYSeries series = chartPolygon.addSeries("Полигон частотей", dataX, dataY);
        series.setMarker(SeriesMarkers.CIRCLE);
        XYSeries series1 = chartPolygon.addSeries("Ось Y", new double[]{dataX.get(0), dataX.get(0)}, new double[]{1.0, 0.0});
        series1.setMarker(SeriesMarkers.DIAMOND);
        series1.setMarkerColor(Color.BLACK);
        series1.setLineColor(Color.BLACK);
        XYSeries series2 = chartPolygon.addSeries("Ось X", new double[]{dataX.get(0), dataX.get(dataX.size() - 1)}, new double[]{0.0, 0.0});
        series2.setMarker(SeriesMarkers.DIAMOND);
        series2.setMarkerColor(Color.BLACK);
        series2.setLineColor(Color.BLACK);
    }

    public void showGraph() {
        new SwingWrapper(chartFunction).displayChart();
        new SwingWrapper(chartPolygon).displayChart();
    }

    public double calculateStep() {
        double maxValue = relativeFrequency.firstKey();
        double minValue = relativeFrequency.lastKey();
        return (maxValue - minValue) /  (1 + log(count));
    }
}
