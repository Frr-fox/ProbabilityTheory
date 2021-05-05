import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.lang.Math.pow;

public class StatisticCharacters {
    @Getter
    private Double[] data;
    @Getter
    private SortedMap<Double, Double> relativeFrequency = new TreeMap<Double, Double>();
    private Double minValue;
    private Double maxValue;
    private Double averageOfDistribution;
    private Double meanSquareDeviation;

    public StatisticCharacters(Double[] data) {
        this.data = data;
    }

    public Double[] buildVariationalSeries() {
        data = sort(data);
        return data;
    }

    public void buildStatisticSeries() {
        for (Double number : data) {
            if (!relativeFrequency.containsKey(number)) {
                relativeFrequency.put(number, 1d);
            } else {
                Double newCount = relativeFrequency.get(number);
                relativeFrequency.put(number, ++newCount);
            }
        }
        relativeFrequency.replaceAll((k, v) -> v / data.length);
    }

    private Double calculateMinValue() {
        minValue = data[0];
        return minValue;
    }

    private Double calculateMaxValue() {
        maxValue = data[data.length - 1];
        return maxValue;
    }

    private Double calculateRangeOfSample() {
        return maxValue - minValue;
    }

    private Double calculateAverageOfDistribution() {
        double sum = 0;
        for (Double number :data) {
            sum += number;
        }
        averageOfDistribution = sum / data.length;
        return averageOfDistribution;
    }

    private Double calculateMeanSquareDeviation() {
        double sum = 0;
        for (Double number :data) {
            sum += pow((number - averageOfDistribution), 2);
        }
        meanSquareDeviation = sum / data.length;
        return meanSquareDeviation;
    }

    public void printCharacteristics() {
        System.out.println("Исходный ряд: ");
        for (Double number : data) {
            System.out.print(number + " ");
        }
        buildStatisticSeries();
        buildVariationalSeries();
        System.out.println("\n\nПолучившийся вариационный ряд: ");
        for (Double number : data) {
            System.out.print(number + " ");
        }
        System.out.println("\n\nПервая порядковая статистика: " + calculateMinValue());
        System.out.println("Двадцатая порядковая статистика: " + calculateMaxValue());
        System.out.println("Размах: " + calculateRangeOfSample());
        System.out.println(String.format("Математическое ожидание: %.3f", calculateAverageOfDistribution())
                .replace(",", "."));
        System.out.println(String.format("Среднеквадратическое отклонение: %.3f", calculateMeanSquareDeviation())
                .replace(",", "."));
    }

    private Double[] sort(Double[] result) {
        if (result.length < 2) {
            return result;
        } else {
            Double[] left = sort(Arrays.copyOfRange(result,0, result.length/2));
            Double[] right = sort(Arrays.copyOfRange(result,result.length / 2, result.length));
            return merge(left, right);
        }
    }

    private Double[] merge(Double[] left, Double[] right) {
        int i = 0, j = 0, k = 0;
        Double[] result = new Double[left.length + right.length];
        while (i < left.length && j < right.length) {
            if (left[i] < right[j]) {
                result[k] = left[i];
                i++;
            } else {
                result[k] = right[j];
                j++;
            }
            k++;
        }
        if (i == left.length) {
            while (j < right.length) {
                result[k] = right[j];
                j++;k++;
            }
        }
        if (j == right.length) {
            while (i < left.length) {
                result[k] = left[i];
                i++;k++;
            }
        }
        return result;
    }
}
