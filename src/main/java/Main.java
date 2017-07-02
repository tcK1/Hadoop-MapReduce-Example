
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
			System.out.println("\tT\tTudo");
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

		String startDate;
		String endDate;
		do {
			do {
				System.out.println("Data de início do intervalo analisado (dd/MM/yyyy):");
				startDate = scanner.nextLine();
				proceedInput = validDate(startDate);
				if (!proceedInput)
					System.out.println("Data inválida.\n");
			} while (!proceedInput);

			do {
				System.out.println("Data de fim do intervalo analisado (dd/MM/yyyy):");
				endDate = scanner.nextLine();
				proceedInput = validDate(endDate);
				if (!proceedInput)
					System.out.println("Data inválida.\n");
			} while (!proceedInput);

			try {
				SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
				proceedInput = dateFormatter.parse(startDate).before(dateFormatter.parse(endDate));
				if (!proceedInput) {
					System.out.println("Data de fim é menor que data de início.\n");
				}
			} catch (ParseException e) {
				proceedInput = false;
			}
		} while (!proceedInput);

		scanner.close();

		/*** Setting job configurations ***/

		Configuration config = new Configuration();
		config.set("startDate", startDate);
		config.set("endDate", endDate);
		config.set("groupingType", groupingType);
		config.set("informationType", informationType);

		/*** Job creation ***/

		FileSystem hdfs = null;
		try {
			hdfs = FileSystem.get(config);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Job job = createJob(config);

		int firstYear = Integer.parseInt((startDate.split("/"))[2]);
		int lastYear = Integer.parseInt((endDate.split("/"))[2]);

		try {
			Path output = new Path("output"); // output := /user/<username>/output
			FileOutputFormat.setOutputPath(job, output);
			if (hdfs.exists(output)) {
				System.out.println("Pasta 'output' existe, mas será apagada.");
				hdfs.delete(output, true);
			}
			for (int year = firstYear; year <= lastYear; year++) {
				Path path = new Path(args[0] + "/" + year);
				if (hdfs.exists(path))
					FileInputFormat.addInputPath(job, path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (job.waitForCompletion(true)) {
				ArrayList<Tuple> tuples = getTupleList(hdfs);
				double[] data = LeastSquares.calculate(tuples);
				new LineChart(tuples, data, informationType);
			} else {
				System.out.println("fim com exit");
				System.exit(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static boolean validDate(String date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			dateFormatter.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
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
		switch (informationType) {
		case "TEMP":
		case "DEWP":
		case "SLP":
		case "STP":
		case "VISIB":
		case "WDSP":
		case "MXSPD":
		case "GUST":
		case "MAX":
		case "MIN":
			return true;
		default:
			return false;
		}
	}

	private static Job createJob(Configuration conf) {
		Job job = null;
		try {
			job = Job.getInstance(conf, "dataweather");
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

	public static ArrayList<Tuple> getTupleList(FileSystem hdfs) throws IOException {
		Path path = new Path("output/part-r-00000");
		BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(path)));
		ArrayList<Tuple> list = new ArrayList<Tuple>();

		String line = reader.readLine();
		do {
			String[] values = line.split("\t");
			list.add(new Tuple(Double.valueOf(values[1]), Double.valueOf(values[2])));
			line = reader.readLine();
		} while (line != null);
		return list;
	}
}
