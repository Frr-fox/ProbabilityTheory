import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.BaseSeriesMarkers;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.*;

import static java.lang.Math.*;

public class Graph {
    private SortedMap<Double, Double> relativeFrequency;
    private final int count;
    private final double start;
    private final double end;
    private final XYChart chartFunction;
    private final XYChart chartPolygon;
    private final XYChart histogram;

    public Graph(SortedMap<Double, Double> relativeFrequency, int count) {
        this.chartFunction = new XYChartBuilder().width(750).height(550).title("Эмпирическая функция распределения")
                .xAxisTitle("X").yAxisTitle("F(x)").build();
        chartFunction.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        this.chartPolygon = new XYChartBuilder().width(650).height(350).title("Полигон частот")
                .xAxisTitle("X").yAxisTitle("n*").build();
        chartPolygon.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
//        this.histogram = new CategoryChartBuilder().width(750).height(550).title("Гистограмма частот")
//                .xAxisTitle("X").yAxisTitle("n/h").build();
        this.histogram = new XYChartBuilder().width(650).height(350).title("Гистограмма частот")
                .xAxisTitle("X").yAxisTitle("n/h").build();
        this.relativeFrequency = relativeFrequency;
        this.count = count;
        double startValue = relativeFrequency.firstKey();
        double endValue = relativeFrequency.lastKey();
        double h = (endValue - startValue) * 0.1;
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
            data_Y.add(frequency / count);
        });
        double sum = 0;
        for (int i = 0; i < data_Y.size(); i++) {
            sum += data_Y.get(i);
            dataY.add(sum);
        }
        dataY.add(1.0);
        dataX.add(end);
        drawAxis(chartFunction, new double[]{start, start}, new double[]{1.0, 0.0},
                new double[]{start, end}, new double[]{0.0, 0.0});
        XYSeries series = chartFunction.addSeries("Эмпирическая функция распределения", dataX, dataY);
        series.setMarker(SeriesMarkers.NONE);
    }

    public void drawPolygonOfFrequencies() {
        chartPolygon.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        ArrayList<Double> dataX = new ArrayList<>(), dataY = new ArrayList<>();
        double h = calculateStep();
        double step = relativeFrequency.firstKey() + h/2;
        int countNumbers = 0;
        for(Map.Entry<Double,Double> entry : relativeFrequency.entrySet()) {
            Double key = entry.getKey();
            Double value = entry.getValue();
            if (key > step) {
                double s = step - h / 2;
                double scale = pow(10, 3);
                double result = ceil(s * scale) / scale;
                dataX.add(result);
                dataY.add((double) countNumbers);
                step += h;
                countNumbers = 0;
                while (key > step) {
                    s = step - h / 2;
                    scale = pow(10, 3);
                    result = ceil(s * scale) / scale;
                    dataX.add(result);
                    dataY.add(0.0);
                    step += h;
                }
            }
            countNumbers += value;
        }
        double s = step - h / 2;
        double scale = pow(10, 3);
        double result = ceil(s * scale) / scale;
        dataX.add(result);
        dataY.add((double) countNumbers);
        drawAxis(chartPolygon, new double[]{start, start}, new double[]{5.0, 0.0},
                new double[]{start, end}, new double[]{0.0, 0.0});
        XYSeries series = chartPolygon.addSeries("Полигон частот", dataX, dataY);
        series.setMarker(SeriesMarkers.CIRCLE);
    }

    public void drawBarGraph() {
        histogram.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        double h = calculateStep();
        ArrayList<Double> dataX = new ArrayList<>(), dataY = new ArrayList<>(), dataXHistogram = new ArrayList<>(),
                dataYHistogram = new ArrayList<>();
        double step = relativeFrequency.firstKey() + h/2;
        int countNumbers = 0;
        for(Map.Entry<Double,Double> entry : relativeFrequency.entrySet()) {
            Double key = entry.getKey();
            Double value = entry.getValue();
            if (key > step) {
                double s = step - h / 2;
                double scale = pow(10, 3);
                double result = ceil(s * scale) / scale;
                dataX.add(result);
                dataXHistogram.add(result - h/2);
                dataY.add(countNumbers / h);
                dataYHistogram.add(countNumbers / h);
                step += h;
                countNumbers = 0;
                while (key > step) {
                    s = step - h / 2;
                    scale = pow(10, 3);
                    result = ceil(s * scale) / scale;
                    dataX.add(result);
                    dataXHistogram.add(result - h/2);
                    dataY.add(0.0);
                    dataYHistogram.add(0.0);
                    step += h;
                }
            }
            countNumbers += value;
        }
        double s = step - h / 2;
        double scale = pow(10, 3);
        double result = ceil(s * scale) / scale;
        dataX.add(result);
        dataXHistogram.add(result - h/2);
        dataY.add(countNumbers / h);
        dataYHistogram.add(countNumbers / h);
        dataXHistogram.add(result + h/2);
        dataYHistogram.add(0.0);
        XYSeries series = histogram.addSeries("Гистограмма частот", dataXHistogram, dataYHistogram);
        series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.StepArea);
        series.setMarker(SeriesMarkers.NONE);
        XYSeries series1 = histogram.addSeries("Полигон частот", dataX, dataY);
        series1.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
    }

    private void drawAxis(XYChart chart, double[] y1, double[] y2, double[] x1, double[] x2) {
        XYSeries series1 = chart.addSeries("Ось Y", y1, y2);
        series1.setMarker(SeriesMarkers.NONE);
        series1.setLineColor(Color.BLACK);
        XYSeries series2 = chart.addSeries("Ось X", x1, x2);
        series2.setMarker(SeriesMarkers.NONE);
        series2.setLineColor(Color.BLACK);
    }

    public void showGraph() {
        new SwingWrapper(chartFunction).displayChart();
        new SwingWrapper(chartPolygon).displayChart();
        new SwingWrapper(histogram).displayChart();
    }

    private double calculateStep() {
        double maxValue = relativeFrequency.lastKey();
        double minValue = relativeFrequency.firstKey();
        return (maxValue - minValue) /  (1 + log(count)/log(2));
    }
}
