
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main {

	static String startDate;

	static String endDate;

	public static void main(String[] args) {

		/*** Input handling ***/

		Scanner scanner = new Scanner(System.in);

		boolean proceedInput = false;

		String groupingType;
		do {
			System.out.println("Selecione o agrupamento dos dados:");
			System.out.println("\tD\tDia");
			System.out.println("\tM\tMês");
			System.out.println("\tA\tAno");
			System.out.println("\tMA\tMês do ano"); // Jan, Fev...
			groupingType = scanner.nextLine().toUpperCase();
			proceedInput = isGroupingType(groupingType);
			if (!proceedInput)
				System.out.println("Seleção inválida. Repita a operação.\n");
		} while (!proceedInput);

		String informationType;
		do {
			System.out.println("Selecione a informação a ser analisada:");
			System.out.println("\tTEMP\tTemperatura"); // 1
			System.out.println("\tDEWP\tPonto de orvalho"); // 2
			System.out.println("\tSLP\tPressão no nível do mar"); // 3
			System.out.println("\tSTP\tPressão na estação"); // 4
			System.out.println("\tVISIB\tVisibilidade"); // 5
			System.out.println("\tWDSP\tVelocidade do vento"); // 6
			System.out.println("\tMXSPD\tVelocidade máxima do vento"); // 7
			System.out.println("\tGUST\tVelocidade máxima da rajada de vento"); // 8
			System.out.println("\tMAX\tTemperatura máxima"); // 9
			System.out.println("\tMIN\tTemperatura mínima"); // 10
			informationType = scanner.nextLine().toUpperCase();
			proceedInput = isInformationType(informationType);
			if (!proceedInput)
				System.out.println("Seleção inválida. Repita a operação.\n");
		} while (!proceedInput);

		scanner.close();

		// Seta os valores a serem usados durante o MapReduce
		Configuration conf = createConf(args[0], args[1], groupingType, informationType);

		FileSystem hdfs = null;
		try {
			hdfs = FileSystem.get(conf);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Job job = createJob(conf);

		String firstYear = (args[0].split("/"))[2];
		int firstYearI = Integer.parseInt(firstYear);
		String lastYear = (args[1].split("/"))[2];
		int lastYearI = Integer.parseInt(lastYear);

		// Itera todos os anos (da data inicial a final), inserindo no
		// caminho de
		// arquivos de cada ano
		String path;
		while (firstYearI <= lastYearI) {
			path = args[2] + "/" + firstYearI;
			try {
				if (hdfs.exists(new Path(path)))
					FileInputFormat.addInputPath(job, new Path(path));

				// Usando output como /user/<usuario>/output
				Path output = new Path("output");
				// Usando output como argumento
				/* Path output = new Path(args[3]); */

				FileOutputFormat.setOutputPath(job, output);

				System.out.println("Deletando a pasta output se ela ja existir");
				// Checa se a pasta de output ja existe, e se existir deleta
				// a mesma
				if (hdfs.exists(output)) {
					hdfs.delete(output, true);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			firstYearI++;
		}

		System.out.println("Input/Output foi");

		try {
			if (job.waitForCompletion(true)) {

				LeastSquares mmq = new LeastSquares();
				ArrayList<Tuple> tuples = getAverageList(hdfs);
				double[] data = mmq.mmq(tuples);
				new LineChart(tuples, data, informationType);
			} else {
				System.out.println("fim");
				System.exit(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("fim");

	}

	private static boolean isGroupingType(String selectionType) {
		switch (selectionType) {
		case "D":
		case "M":
		case "A":
		case "MA":
		case "T":
			return true;
		default:
			return false;
		}
	}

	private static boolean isInformationType(String informationType) {
		// TODO Auto-generated method stub
		return false;
	}

	private static Job createJob(Configuration conf) {
		Job job = null;
		try {
			// Cria o job a ser executado
			job = Job.getInstance(conf, "dataweather");

			// Cria uma instancia do sistema de arquivos para podemos consultar
			// os arquivos

		} catch (IOException e) {
			System.out.println("Não foi possível criar o job");
			System.err.println(e);
			e.printStackTrace();
		}

		job.setJarByClass(Main.class);
		job.setMapperClass(WeatherMapper.class);
		job.setReducerClass(StatisticReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		return job;
	}

	private static Configuration createConf(String startDate, String endDate, String selectionType,
			String informationType) {
		Configuration conf = new Configuration();
		conf.set("startDate", startDate);
		conf.set("endDate", endDate);
		conf.set("selectionType", selectionType);
		conf.set("informationType", informationType);
		return conf;
	}

	public static ArrayList<Tuple> getAverageList(FileSystem hdfs) throws IOException {
		Path path = new Path("output/part-r-00000");
		BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(path)));
		ArrayList<Tuple> list = new ArrayList<Tuple>();
		String line = reader.readLine();

		double avg;
		double dev;
		int aux = 1;
		while (line != null) {
			String[] splitLine = line.split(" ");

			for (int i = 0; i < splitLine.length; i++) {
				System.out.print(i + ": " + splitLine[i] + " / ");
			}
			System.out.println();

			String[] values = splitLine[1].split("	");

			System.out.println(splitLine[1]);
			// String date = values[0];
			avg = Double.valueOf(values[1]);

			line = reader.readLine();
			splitLine = line.split(" ");
			values = splitLine[1].split("	");

			System.out.println(splitLine[1]);
			dev = Double.valueOf(values[1]);

			list.add(new Tuple(avg, dev));
			// esse aux é só pra imprimir. quando tirar o print, tirar o aux
			System.out.println("tupla adicionada: " + aux + " " + avg + " " + dev);
			line = reader.readLine();
			aux++;
		}

		return list;
	}
}
