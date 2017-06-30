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

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private boolean isValidDate(String actualDate, String startDate, String endDate) throws ParseException {
		Date actual = dateFormat.parse(actualDate);
		Date start = dateFormat.parse(startDate);
		Date end = dateFormat.parse(endDate);

		if (actual.after(start) && actual.before(end)) {
			System.out.println("data " + actualDate + " valida");
			return true;
		}
		System.out.println("data " + actualDate + " INvalida");
		return false;
	}

	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, DoubleWritable>.Context context)
			throws IOException, InterruptedException {
		// final double MISSING4 = 9999.9;
		// final double MISSING3 = 999.9;
		System.out.println("Comecou o mapper");

		String line = value.toString();
		System.out.println("lendo linha " + line);

		// o arquivo começa com "STN"... Ignorar primeira linha
		if (line.startsWith("S")) {
			return;
		}

		String year = line.substring(14, 18);
		String month = line.substring(18, 20);
		String day = line.substring(20, 22);
		System.out.println("data: " + day + "/" + month + "/" + year);
		String date = day + "/" + month + "/" + year;

		// verifica se a data lida é para ser analisada
		try {
			Configuration conf = context.getConfiguration();
			String startDate = conf.get("startDate");
			String endDate = conf.get("endDate");

			if (!isValidDate(date, startDate, endDate)) {
				return;
			}
		} catch (ParseException e) {
			System.err.println("Data no formato invalido ");
			e.printStackTrace();
		}

		// se tudo certo ate agora, começa as contas
		double information = Double.parseDouble(line.substring(24, 30)); // temperatura
		//information = Double.parseDouble(line.substring(35, 41)); // dewp
		//information = Double.parseDouble(line.substring(46, 52)); // slp
		//information = Double.parseDouble(line.substring(57, 63)); // stp
		//information = Double.parseDouble(line.substring(68, 73)); // visib
		//information = Double.parseDouble(line.substring(78, 83)); // wdsp
		//information = Double.parseDouble(line.substring(88, 93)); // mxspd
		//information = Double.parseDouble(line.substring(95, 100)); // gust
		
        // falta temperatura maxima
		System.out.println("indo para o reducer " + date + " " + information);
		context.write(new Text(date), new DoubleWritable(information));
	}
}
