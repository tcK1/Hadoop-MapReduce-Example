import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart {

	ArrayList<Tuple> tuple;

	double[] mmq;

	public LineChart(ArrayList<Tuple> tuple, double[] mmq, String information) throws IOException {
		this.tuple = tuple;
		this.mmq = mmq;

		JFreeChart chart = createChartPanel(information);

		ChartUtilities.saveChartAsJPEG(new java.io.File("lineChart.jpg"), chart, 700, 600);

	}

	private JFreeChart createChartPanel(String information) {
		// creates a line chart object
		// returns the chart panel
		String chartTitle = "Grafico LINDÃO";
		String xAxisLabel = "Data";
		String yAxisLabel = information;

		XYDataset dataset = createDataset();
		System.setProperty("java.awt.headless", "true");
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);

		XYPlot plot = chart.getXYPlot();

		plot.setBackgroundPaint(new Color(225, 225, 225));

		XYItemRenderer xyir = plot.getRenderer();
		xyir.setSeriesPaint(0, Color.RED);

		return chart;
	}

	private XYDataset createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries avgMoreSerie = new XYSeries("Média + desvio padrão");
		XYSeries avgSerie = new XYSeries("Média");
		XYSeries avgLessSerie = new XYSeries("Média - desvio padrão");
		XYSeries mmqSerie = new XYSeries("Mínimo quadrado");

		for (int i = 0; i < tuple.size(); i++) {
			double value = this.mmq[0] + (this.mmq[1] * i + 1);
			Tuple t = tuple.get(i);

			avgSerie.add(i + 1, t.average);
			avgMoreSerie.add(i + 1, t.average + t.standardDeviation);
			avgLessSerie.add(i + 1, t.average - t.standardDeviation);

			System.out.println("valores mmq: " + i + ", " + value);

			mmqSerie.add(i + 1, value);
		}

		dataset.addSeries(avgLessSerie);
		dataset.addSeries(avgMoreSerie);
		dataset.addSeries(avgSerie);
		dataset.addSeries(mmqSerie);

		return dataset;
	}

}
