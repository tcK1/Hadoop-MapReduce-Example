import java.util.ArrayList;

public class LeastSquares {

	public static double average(ArrayList<Double> axis) {
		double total = 0;
		for (int i = 0; i < axis.size(); i++) {
			total += (axis.get(i));
		}
		return total / axis.size();
	}

	public double[] mmq(ArrayList<Tuple> axis) {

		// Le o arraylist com label e info e pega só o label para as contas.
		ArrayList<Double> yAxis = new ArrayList<Double>();
		for (int i = 0; i < axis.size(); i++) {
			yAxis.add(axis.get(i).avg);
		}

		// double xAxisAvg = average(xAxis);
		double xAxisAvg = yAxis.size() / 2;
		double yAxisAvg = average(yAxis);

		double topPart = 0;
		double bottomPart = 0;

		for (int i = 0; i < yAxis.size(); i++) {

			// System.out.println("xAxis: " + xAxis.get(i));
			// System.out.println("yAxis: " + yAxis.get(i));
			// System.out.println("yAxisAvg: " + yAxisAvg);
			topPart += i * (yAxis.get(i) - yAxisAvg);
			bottomPart += i * (i - xAxisAvg);

		}
		System.out.println("topPart: " + topPart);
		System.out.println("bottomPart: " + bottomPart);

		double b = (topPart / bottomPart);
		double a = yAxisAvg - (b * xAxisAvg);

		double[] end = { a, b };
		System.out.println("a: " + a);
		System.out.println("b: " + b);
		return end;
	}
}
