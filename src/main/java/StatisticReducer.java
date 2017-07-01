import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class StatisticReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	private MultipleOutputs<Text, DoubleWritable> mos;

	private double average(double total, int quantity) {
		return total / quantity;
	}

	private double standardDeviation(Iterable<DoubleWritable> values, int quantity, double average) {
		double temp = 0;
		for (DoubleWritable value : values) temp += Math.pow((value.get() - average), 2.0);
		return Math.sqrt(temp / (quantity-1));
	}

	private double leastSquares(List<Double> x, List<Double> y) {
		return 0D;
	}

	@Override
	protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {
		System.out.println("Comecou o reducer");
		Configuration conf = context.getConfiguration();

		double total = 0.0;
		int qnt = 0;

		for (DoubleWritable value : values) {
			total += value.get();
			qnt++;
		}

		double average = average(total, qnt);
		double standardDeviation = standardDeviation(values, qnt, average);

		// DoubleWritable dw = new DoubleWritable(average);
		// mos.write("mean", key, dw);
		// dw.set(standarDeviation);
		// mos.write("standard-deviation", key, dw);
		context.write(key, new DoubleWritable(average));

	}

	@Override
	protected void cleanup(Reducer<Text, DoubleWritable, Text, DoubleWritable>.Context context)
			throws java.io.IOException, InterruptedException {
		mos.close();
	}

	@Override()
	protected void setup(Reducer<Text, DoubleWritable, Text, DoubleWritable>.Context context)
			throws java.io.IOException, java.lang.InterruptedException {
		mos = new MultipleOutputs(context);
	}
}
