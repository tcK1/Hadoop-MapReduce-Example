import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
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
		System.out.println("createchartpanel");
		// creates a line chart object
		// returns the chart panel
		String chartTitle = "Grafico lindão";
		String xAxisLabel = "Data";
		String yAxisLabel = "Temperaturaaaa";

		XYDataset dataset = createDataset();
		System.setProperty("java.awt.headless", "true");
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);

		return chart;
	}

	private XYDataset createDataset() {
		System.out.println("createdataset");
		// creates an XY dataset...
		// returns the dataset
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries avgSerie = new XYSeries("Avg");
		XYSeries devSerie = new XYSeries("Dev");
		XYSeries mmqSerie = new XYSeries("Mmq");

		System.out.println(tuple.size() + " size do tuple");
		System.out.println(mmq.length + " size do mmq");
		for (int i = 0; i < tuple.size(); i++) {
			// System.out.println("adicionou valor no grafico: " + xValue.get(i)
			// + " " + yValue.get(i));
			String date = tuple.get(i).date;
			String[] bDate = date.split("/");
			int length = bDate.length;

			switch (length) {
			case 1:
				date = bDate[0];
				break;
			case 2:
				date = bDate[1] + bDate[0];
				break;
			case 3:
				date = bDate[2] + bDate[1] + bDate[0];
				break;
			}

			double comparableDate = Double.parseDouble(date);

			double value = this.mmq[0] + (this.mmq[1] * comparableDate);

			avgSerie.add(comparableDate, tuple.get(i).avg);
			devSerie.add(comparableDate, tuple.get(i).dev);

			System.out.println("valores mmq: " + comparableDate + ", " + value);

			mmqSerie.add(comparableDate, value);
			// mmq.add(xValue.get(i), yValue.get(i));
			// series1.add(1, 2);
			// series1.add(2, 2);
			// series1.add(3, 2);
		}

		dataset.addSeries(avgSerie);
		dataset.addSeries(devSerie);
		dataset.addSeries(mmqSerie);

		return dataset;
	}

}
