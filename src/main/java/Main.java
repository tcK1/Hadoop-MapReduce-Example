
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main {

	// se for comodo, é possivel alterar a origem e a saida dos dados
	private final static String INPUT_PATH = "";

	private final static String OUTPUT_PATH = "";

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	// mapper
	// public static class WeatherMapper extends Mapper<LongWritable, Text,
	// Text, DoubleWritable> {
	// }
	//
	// // reducer
	// public static class StatisticReducer extends Reducer<Text,
	// DoubleWritable, Text, DoubleWritable> {
	// }

	private java.util.Date getDate(String date) {
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			System.out.println("Não foi possível converter a data");
			System.err.println(e);
		}
		return null;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.println("insisra a data de inicio no formato DD/MM/AAAA");
		String startDate = sc.nextLine();
		System.out.println("insira a data final no formato DD/MM/AAAA");
		String endDate = sc.nextLine();

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
		System.out.println("Pressao nivel do mar: 3"); // SLP
		System.out.println("Pressao na estacao: 4"); // STP
		System.out.println("Visibilidade: 5"); // VISIB
		System.out.println("Velocidade do vento: 6"); // WDSP
		System.out.println("Velocidade maxima do vento: 7"); // MXSPD
		System.out.println("Velocidade maxima da rajada de vento: 8"); // GUST
		String tipoInformacao = sc.nextLine().toUpperCase();

		sc.close();

		Configuration conf = new Configuration();
		try {
			Job job = Job.getInstance(conf, "dataweather");
			job.setJarByClass(Main.class);

			FileInputFormat.addInputPath(job, new Path(INPUT_PATH));
			FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));
		} catch (IOException e) {
			System.out.println("Não foi possível criar o job");
			System.err.println(e);
		}

	}
}
