import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart {

	ArrayList<Tuple> tuples;

	double[] leastSquares;

	public LineChart(ArrayList<Tuple> tuples, double[] leastSquares, String information, String startDate,
			String endDate) throws IOException {
		this.tuples = tuples;
		this.leastSquares = leastSquares;

		JFreeChart chart = createChartPanel(information, startDate, endDate);
		ChartUtilities.saveChartAsJPEG(new java.io.File("lineChart.jpg"), chart, 700, 600);
	}

	private JFreeChart createChartPanel(String information, String startDate, String endDate) {
		String chartTitle = startDate + " até " + endDate;
		String xAxisLabel = "Data";
		String yAxisLabel = information;

		XYDataset dataset = createDataset();
		System.setProperty("java.awt.headless", "true");
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);

		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(new Color(225, 225, 225));
		return chart;
	}

	private XYDataset createDataset() {
		XYSeries avgMoreSerie = new XYSeries("Média + desvio padrão");
		XYSeries avgSerie = new XYSeries("Média");
		XYSeries avgLessSerie = new XYSeries("Média - desvio padrão");
		XYSeries squaresSerie = new XYSeries("Mínimo quadrado");

		for (int i = 0; i < tuples.size(); i++) {
			double value = this.leastSquares[0] + (this.leastSquares[1] * i + 1);
			Tuple tuple = tuples.get(i);
			avgSerie.add(i + 1, tuple.average);
			avgMoreSerie.add(i + 1, tuple.average + tuple.standardDeviation);
			avgLessSerie.add(i + 1, tuple.average - tuple.standardDeviation);
			squaresSerie.add(i + 1, value);
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(avgLessSerie);
		dataset.addSeries(avgMoreSerie);
		dataset.addSeries(avgSerie);
		dataset.addSeries(squaresSerie);
		return dataset;
	}

}
