public class Application {

  public static void main(String[] args) {
    Double[] data = new Double[] {0.9, 0.24, 0.55, -1.45, 0.17,
                                -1.0, 0.62, -1.45, -0.52, -1.31,
                                -0.76, -0.55, -0.62, 0.21, -1.31,
                                -1.14, 1.07, -0.14, -1.45, 1.45};
    StatisticCharacters statisticCharacters = new StatisticCharacters(data);
    statisticCharacters.printCharacteristics();
    Graph graph = new Graph(statisticCharacters.getRelativeFrequency(), statisticCharacters.getData().length);
    graph.drawFunction();
    graph.drawPolygonOfFrequencies();
    graph.showGraph();
  }
}
