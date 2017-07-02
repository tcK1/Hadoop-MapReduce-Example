import java.util.ArrayList;

public class LeastSquares {

	public static double average(ArrayList axis) {
		double total = 0;
		for (int i = 0; i < axis.size(); i++) {
			total += (double) axis.get(i);
		}
		return total / axis.size();
	}

	public static double[] mmq(ArrayList<Tuple> axis) {

		// Le o arraylist com label e info e pega só o label para as contas.
		ArrayList yAxis = new ArrayList();
		for (int i = 0; i < axis.size(); i++) {
			yAxis.add(axis.get(i).avg);
		}

		// Faz um arraylist de 0 até n onde n é a quantidade de datas
		ArrayList xAxis = new ArrayList();
		for (int i = 0; i < xAxis.size(); i++) {
			xAxis.add(i);
		}

		double xAxisAvg = average(xAxis);
		double yAxisAvg = average(yAxis);

		double topPart = 0;
		double bottomPart = 0;

		for (int i = 0; i < xAxis.size(); i++) {
			topPart += (double) xAxis.get(i) * ((double) yAxis.get(i) - yAxisAvg);
			bottomPart += (double) xAxis.get(i) * ((double) xAxis.get(i) - xAxisAvg);
		}

		double b = (topPart / bottomPart);
		double a = yAxisAvg - (b * xAxisAvg);

		double[] end = { a, b };
		return end;
	}
}
