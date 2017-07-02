import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WeatherMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {

	static final double MISSING_4 = 9999.9;
	static final double MISSING_3 = 999.9;

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

	private String getSelectionType(String dateFormat, String date) throws IllegalArgumentException {
		switch (dateFormat) {
		case "D":
			return date.substring(0, 2);
		case "M":
			return date.substring(3, 5);
		case "A":
			return date.substring(6, 10);
		case "MA":
			return date.substring(3, 10);
		case "T":
			return date;
		default:
			throw new IllegalArgumentException("Invalid date format: '" + dateFormat + "'");
		}

	}

	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, DoubleWritable>.Context context)
			throws IOException, InterruptedException {

		String line = value.toString();
		if (line.startsWith("STN")) // Header line
			return;

		String year = line.substring(14, 18);
		String month = line.substring(18, 20);
		String day = line.substring(20, 22);
		String dateString = day + "/" + month + "/" + year;

		Configuration conf = context.getConfiguration();

		try {
			Date date = dateFormatter.parse(dateString);
			Date intervalStart = dateFormatter.parse(conf.get("startDate"));
			Date intervalEnd = dateFormatter.parse(conf.get("endDate"));
			if (date.before(intervalStart) || date.after(intervalEnd)) {
				return;
			}
		} catch (ParseException e) {
			System.err.println("Invalid date format.");
			e.printStackTrace();
		}

		double information = 0;
		boolean validData = false;
		switch (Integer.parseInt(conf.get("informationType"))) {
		case 1:
			information = Double.parseDouble(line.substring(24, 30)); // temperatura
			validData = information != MISSING_4;
			break;
		case 2:
			information = Double.parseDouble(line.substring(35, 41)); // dewp
			validData = information != MISSING_4;
			break;
		case 3:
			information = Double.parseDouble(line.substring(46, 52)); // slp
			validData = information != MISSING_4;
			break;
		case 4:
			information = Double.parseDouble(line.substring(57, 63)); // stp
			validData = information != MISSING_4;
			break;
		case 5:
			information = Double.parseDouble(line.substring(68, 73)); // visib
			validData = information != MISSING_3;
			break;
		case 6:
			information = Double.parseDouble(line.substring(78, 83)); // wdsp
			validData = information != MISSING_3;
		case 7:
			information = Double.parseDouble(line.substring(88, 93)); // mxspd
			validData = information != MISSING_3;
		case 8:
			information = Double.parseDouble(line.substring(95, 100)); // gust
			validData = information != MISSING_3;
			break;
		case 9:
			information = Double.parseDouble(line.substring(102, 108)); // temp
			validData = information != MISSING_3;
			break;
		default:
			System.out.println("Tipo de informacao invalido. Deve ser um numero entre 1 a 9. "
					+ Integer.parseInt(conf.get("informationType")));
			break;
		}

		String group = "";
		group = getSelectionType(conf.get("selectionType"), dateString);

		if (validData) {
			context.write(new Text(group), new DoubleWritable(information));
		}

	}
}
