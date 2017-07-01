import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart extends JFrame {

	private static final long serialVersionUID = -760069495041235989L;

	List<Double> xValue;

	List<Double> yValue;

	public LineChart(List<Double> xValue, List<Double> yValue) {
		super("XY Line Chart Example with JFreechart");

		this.xValue = xValue;
		this.yValue = yValue;

		JPanel chartPanel = createChartPanel();
		add(chartPanel, BorderLayout.CENTER);

		setSize(640, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	private JPanel createChartPanel() {
		// creates a line chart object
		// returns the chart panel
		String chartTitle = "Grafico lindão";
		String xAxisLabel = "Data";
		String yAxisLabel = "Temperaturaaaa";

		XYDataset dataset = createDataset();

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);

		return new ChartPanel(chart);
	}

	private XYDataset createDataset() {
		// creates an XY dataset...
		// returns the dataset
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Graph");

		for (int i = 0; i < xValue.size(); i++) {
			series1.add(xValue.get(i), yValue.get(i));
		}

		dataset.addSeries(series1);

		return dataset;
	}

	public void drawGraph(List<Double> xValue, List<Double> yValue) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new LineChart(xValue, yValue).setVisible(true);
			}
		});
	}
}
