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

	public LineChart(ArrayList<Tuple> tuple, double[] mmq) throws IOException {
		this.tuple = tuple;
		this.mmq = mmq;

		JFreeChart chart = createChartPanel();

		ChartUtilities.saveChartAsJPEG(new java.io.File("lineChart.jpg"), chart, 700, 600);

	}

	private JFreeChart createChartPanel() {
		// creates a line chart object
		// returns the chart panel
		String chartTitle = "Grafico LINDÃO";
		String xAxisLabel = "Data";
		String yAxisLabel = "Temperaturaaaa";

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
		XYSeries avgMoreSerie = new XYSeries("AvgMore");
		XYSeries avgSerie = new XYSeries("Média");
		XYSeries avgLessSerie = new XYSeries("AvgLess");
		XYSeries mmqSerie = new XYSeries("Mínimo quadrado");

		for (int i = 0; i < tuple.size(); i++) {
			int date = tuple.get(i).date;
			// String[] bDate = date.split("/");
			// int length = bDate.length;
			//
			// switch (length) {
			// case 1:
			// date = bDate[0];
			// break;
			// case 2:
			// date = bDate[1] + bDate[0];
			// break;
			// case 3:
			// date = bDate[2] + bDate[1] + bDate[0];
			// break;
			// }

			// double comparableDate = Double.parseDouble(date);

			double value = this.mmq[0] + (this.mmq[1] * date);
			Tuple t = tuple.get(i);

			avgSerie.add(date, t.avg);
			avgMoreSerie.add(date, t.avg + t.dev);
			avgLessSerie.add(date, t.avg - t.dev);

			System.out.println("valores mmq: " + date + ", " + value);

			mmqSerie.add(date, value);
		}

		dataset.addSeries(avgLessSerie);
		dataset.addSeries(avgMoreSerie);
		dataset.addSeries(avgSerie);
		dataset.addSeries(mmqSerie);

		return dataset;
	}

}
