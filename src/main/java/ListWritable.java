import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class ListWritable implements Writable {
	Double average;
	Double standardDeviation;

	public ListWritable(double average, double standardDeviation) {
		this.average = average;
		this.standardDeviation = standardDeviation;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		average = in.readDouble();
		standardDeviation = in.readDouble();

	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeDouble(average);
		out.writeDouble(standardDeviation);
	}

}
