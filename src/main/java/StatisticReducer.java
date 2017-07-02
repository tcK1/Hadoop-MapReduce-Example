
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class StatisticReducer extends Reducer<Text, DoubleWritable, Text, TupleWritable> {

	private MultipleOutputs<Text, TupleWritable> mos;

	private double average(List<Double> values) {
		double total = 0;
		for (double v : values) {
			total = total + v;
		}
		return total / values.size();
	}

	private double standardDeviation(List<Double> values, double average) {
		double sum = 0;
		for (double value : values)
			sum += Math.pow(value - average, 2.0);
		return Math.sqrt(sum / values.size());
	}

	@Override
	protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {

		List<Double> allValues = new ArrayList<Double>();

		for (DoubleWritable value : values)
			allValues.add(value.get());

		double average = average(allValues);
		double standardDeviation = standardDeviation(allValues, average);

		context.write(new Text(key), new TupleWritable(average, standardDeviation));
	}

	@Override
	protected void cleanup(Reducer<Text, DoubleWritable, Text, TupleWritable>.Context context)
			throws java.io.IOException, InterruptedException {
		mos.close();
	}

	@Override()
	protected void setup(Reducer<Text, DoubleWritable, Text, TupleWritable>.Context context)
			throws java.io.IOException, java.lang.InterruptedException {
		mos = new MultipleOutputs<Text, TupleWritable>(context);
	}
}
