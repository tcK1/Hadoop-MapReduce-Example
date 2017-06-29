import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.tools.ant.taskdefs.MacroDef.Text;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

public class StatisticReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	private MultipleOutputs<Text, DoubleWritable> mos;

	Log log = LogFactory.getLog(WeatherMapper.class);

	private double average(double total, int quantity) {
		return total / quantity;
	}

	private double standardDeviation(Iterable<DoubleWritable> values, int quantity, double average) {
		double aux = 0;
		for (DoubleWritable value : values) {
			aux = aux + Math.pow(value.get() - average, 2.0);
		}
		return Math.sqrt(aux / quantity);
	}

	private double leastSquares(List<Double> x, List<Double> y) {
		return 0D;
	}

	@Override
	protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {
		log.debug("Comecou o reducer");
		Configuration conf = context.getConfiguration();

		double total = 0.0;
		int aux = 0;

		for (DoubleWritable value : values) {
			total = total + value.get();
			aux++;
		}

		double average = average(total, aux);
		double standarDeviation = standardDeviation(values, aux, average);

		DoubleWritable dw = new DoubleWritable(average);
		mos.write("mean", key, dw);
		dw.set(standarDeviation);
		mos.write("standart-deviation", key, dw);

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
