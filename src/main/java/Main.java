
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.tools.ant.taskdefs.MacroDef.Text;

public class Main {

	static String startDate;

	static String endDate;

	// mapper
	public static class WeatherMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {

		private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		private boolean isValidDate(Date actual, String startDate, String endDate) throws ParseException {
			Date start = dateFormat.parse(startDate);
			Date end = dateFormat.parse(endDate);

			if (actual.after(start) && actual.before(end)) {
				return true;
			}
			return false;
		}

		@Override
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, Text, DoubleWritable>.Context context)
				throws IOException, InterruptedException {

			String line = value.toString();

			// o arquivo começa com "STN"... Ignorar primeira linha
			if (line.startsWith("S")) {
				return;
			}

			int year = Integer.parseInt(line.substring(14, 18));
			int month = Integer.parseInt(line.substring(18, 20));
			int day = Integer.parseInt(line.substring(20, 21));
			System.out.println("data: " + day + "/" + month + "/" + year);

			// verifica se a data lida é para ser analisada
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.clear();
				calendar.set(year, month, day);
				Date actual = calendar.getTime();
				if (isValidDate(actual, startDate, endDate)) {
					return;
				}
			} catch (ParseException e) {
				System.err.println("Data no formato invalido ");
				e.printStackTrace();
			}

			// se tudo certo ate agora, começa as contas

		}
	}

	// reducer
	public static class StatisticReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

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

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.println("insisra a data de inicio no formato DD/MM/AAAA");
		startDate = sc.nextLine();
		System.out.println("insira a data final no formato DD/MM/AAAA");
		endDate = sc.nextLine();

		System.out.println("Como deseja que os dados sejam agrupados?");
		System.out.println("Ano-Mes-Dia: T"); // Tudo
		System.out.println("Ano-Mes: P"); // Parcial
		System.out.println("Ano: A"); // Ano
		System.out.println("Mes: M"); // Mes
		System.out.println("Dia: D"); // Dia
		String tipoSelecao = sc.nextLine().toUpperCase();

		System.out.println("Qual informacao deseja analisar?");
		System.out.println("Temperatura: 1"); // TEMP
		System.out.println("Ponto de orvalho: 2"); // DEWP
		System.out.println("Pressao no nivel do mar: 3"); // SLP
		System.out.println("Pressao na estacao: 4"); // STP
		System.out.println("Visibilidade: 5"); // VISIB
		System.out.println("Velocidade do vento: 6"); // WDSP
		System.out.println("Velocidade maxima do vento: 7"); // MXSPD
		System.out.println("Velocidade maxima da rajada de vento: 8"); // GUST
		System.out.println("Temperatura Maxima: 9");// MAX
		String tipoInformacao = sc.nextLine().toUpperCase();

		sc.close();

		Configuration conf = new Configuration();
		try {
			Job job = Job.getInstance(conf, "dataweather");
			job.setJarByClass(Main.class);

			FileInputFormat.addInputPath(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, new Path(args[1]));
		} catch (IOException e) {
			System.out.println("Não foi possível criar o job");
			System.err.println(e);
		}

	}
}
