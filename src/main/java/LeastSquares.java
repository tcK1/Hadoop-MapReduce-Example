import java.util.ArrayList;

public class LeastSquares {

	public static double average(ArrayList<Double> axis) {
		double total = 0;
		for (double value : axis)
			total += value;
		return total / axis.size();
	}

	public static double[] calculate(ArrayList<Tuple> axis) {

		ArrayList<Double> yAxis = new ArrayList<Double>();
		for (Tuple tuple : axis)
			yAxis.add(tuple.average);

		// double xAxisAvg = average(xAxis);
		double xAvg = yAxis.size() / 2;
		double yAvg = average(yAxis);

		double topPart = 0;
		double bottomPart = 0;

		for (int i = 0; i < yAxis.size(); i++) {
			topPart += (i + 1) * (yAxis.get(i) - yAvg);
			bottomPart += (i + 1) * ((i + 1) - xAvg);
		}

		double b = (topPart / bottomPart);
		double a = yAvg - (b * xAvg);

		double[] end = { a, b };
		return end;
	}
}
