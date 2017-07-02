import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart {

	ArrayList<Tuple> avg;

	ArrayList<Tuple> dev;

	double[] mmq;

	public LineChart(ArrayList<Tuple> avg, ArrayList<Tuple> dev, double[] mmq) throws IOException {
		this.avg = avg;
		this.dev = dev;
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

		System.out.println(avg.size() + " size do avg");
		System.out.println(dev.size() + " size do dev");
		System.out.println(mmq.length + " size do mmq");
		for (int i = 0; i < avg.size() && i < dev.size(); i++) {
			// System.out.println("adicionou valor no grafico: " + xValue.get(i)
			// + " " + yValue.get(i));
			String date = avg.get(i).label;
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

			avgSerie.add(comparableDate, avg.get(i).info);
			devSerie.add(comparableDate, dev.get(i).info);
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
