import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.tools.ant.taskdefs.MacroDef.Text;

public class WeatherMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private boolean isValidDate(String actualDate, String startDate, String endDate) throws ParseException {
		Date actual = dateFormat.parse(actualDate);
		Date start = dateFormat.parse(startDate);
		Date end = dateFormat.parse(endDate);

		if (actual.after(start) && actual.before(end)) {
			return true;
		}
		return false;
	}

	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, DoubleWritable>.Context context)
			throws IOException, InterruptedException {

		String line = value.toString();

		// o arquivo começa com "STN"... Ignorar primeira linha
		if (line.startsWith("S")) {
			return;
		}

		String year = line.substring(14, 18);
		String month = line.substring(18, 20);
		String day = line.substring(20, 21);
		System.out.println("data: " + day + "/" + month + "/" + year);
		String date = day + "/" + month + "/" + "/" + year;

		// verifica se a data lida é para ser analisada
		try {
			Configuration conf = context.getConfiguration();
			String startDate = conf.get("startDate");
			String endDate = conf.get("endDate");

			if (isValidDate(date, startDate, endDate)) {
				return;
			}
		} catch (ParseException e) {
			System.err.println("Data no formato invalido ");
			e.printStackTrace();
		}

		// se tudo certo ate agora, começa as contas

	}
}