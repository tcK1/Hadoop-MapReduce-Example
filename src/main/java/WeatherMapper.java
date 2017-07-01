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

	static final double MISSING4 = 9999.9;
	static final double MISSING3 = 999.9;

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

	private String getSelectionType(String selectionType, String date) throws IllegalArgumentException {
		switch (selectionType) {
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
			throw new IllegalArgumentException("tipo de agrupamento invalido " + selectionType);
		}

	}

	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, DoubleWritable>.Context context)
			throws IOException, InterruptedException {

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
		Configuration conf = context.getConfiguration();
		String startDate = conf.get("startDate");
		String endDate = conf.get("endDate");
		try {
			if (!isValidDate(date, startDate, endDate)) {
				return;
			}
		} catch (ParseException e) {
			System.err.println("Data no formato invalido ");
			e.printStackTrace();
		}

		// se tudo certo ate agora, começa as contas
		double information = 0;
		boolean validData = false;
		switch (Integer.parseInt(conf.get("informationType"))) {
		case 1:
			information = Double.parseDouble(line.substring(24, 30)); // temperatura
			validData = information != MISSING4;
			break;
		case 2:
			information = Double.parseDouble(line.substring(35, 41)); // dewp
			validData = information != MISSING4;
			break;
		case 3:
			information = Double.parseDouble(line.substring(46, 52)); // slp
			validData = information != MISSING4;
			break;
		case 4:
			information = Double.parseDouble(line.substring(57, 63)); // stp
			validData = information != MISSING4;
			break;
		case 5:
			information = Double.parseDouble(line.substring(68, 73)); // visib
			validData = information != MISSING3;
			break;
		case 6:
			information = Double.parseDouble(line.substring(78, 83)); // wdsp
			validData = information != MISSING3;
		case 7:
			information = Double.parseDouble(line.substring(88, 93)); // mxspd
			validData = information != MISSING3;
		case 8:
			information = Double.parseDouble(line.substring(95, 100)); // gust
			validData = information != MISSING3;
			break;
		case 9:
			information = Double.parseDouble(line.substring(102, 108)); // temp
			validData = information != MISSING3;
			break;
		default:
			System.out.println("Tipo de informacao invalido. Deve ser um numero entre 1 a 9. "
					+ Integer.parseInt(conf.get("informationType")));
			break;
		}

		String group = "";
		group = getSelectionType(conf.get("selectionType"), date);

		if (validData) {
			System.out.println("indo para o reducer " + group + " " + information);
			context.write(new Text(group), new DoubleWritable(information));
		}

	}
}
