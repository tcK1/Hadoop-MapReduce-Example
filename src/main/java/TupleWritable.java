import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Writable;

public class TupleWritable implements Writable {

	private DoubleWritable average;
	private DoubleWritable standardDeviation;

	public TupleWritable(Tuple tuple) {
		this.average = new DoubleWritable(tuple.average);
		this.standardDeviation = new DoubleWritable(tuple.standardDeviation);
	}

	public TupleWritable(double average, double standardDeviation) {
		this.average = new DoubleWritable(average);
		this.standardDeviation = new DoubleWritable(standardDeviation);
	}

	@Override
	public void readFields(DataInput input) throws IOException {
		this.average.readFields(input);
		this.standardDeviation.readFields(input);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		this.average.write(output);
		this.standardDeviation.write(output);
	}

	@Override
	public String toString() {
		return this.average + "\t" + this.standardDeviation;
	}

}
