import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.tools.ant.taskdefs.MacroDef.Text;

public class StatisticReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	private double average(double total, int quantity) {
		return total / quantity;
	}

	private double standardDeviation(double[] values, int quantity, double average) {
		double aux = 0;
		for (double d : values) {
			aux = aux + Math.pow(d - average, 2.0);
		}
		return Math.sqrt(aux / quantity);
	}

	@Override
	protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {

		double total = 0.0;
		int aux = 0;

		for (DoubleWritable value : values) {
			total = total + value.get();
			aux++;
		}
		double average = average(total, aux);

		context.write(key, new DoubleWritable(average));
	}
}
