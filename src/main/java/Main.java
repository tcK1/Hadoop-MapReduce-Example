
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
		Scanner sc = new Scanner(System.in);

		System.out.println("Como deseja que os dados sejam agrupados?");

		System.out.println("D		-> Dia"); // Dia
		System.out.println("M		-> Mes"); // Mes
		System.out.println("A		-> Ano"); // Ano
		System.out.println("MA	-> Mes do ano"); // Jan, fev...
		String selectionType = sc.nextLine().toUpperCase();

		System.out.println("Qual informacao deseja analisar?");

		System.out.println("1 -> Temperatura"); // TEMP
		System.out.println("2 -> Ponto de orvalho"); // DEWP
		System.out.println("3 -> Pressao no nivel do mar"); // SLP
		System.out.println("4 -> Pressao na estacao"); // STP
		System.out.println("5 -> Visibilidade"); // VISIB
		System.out.println("6 -> Velocidade do vento"); // WDSP
		System.out.println("7 -> Velocidade maxima do vento"); // MXSPD
		System.out.println("8 -> Velocidade maxima da rajada de vento"); // GUST
		System.out.println("9 -> Temperatura Maxima");// MAX
		String informationType = sc.nextLine().toUpperCase();

		sc.close();

		// Seta os valores a serem usados durante o MapReduce
		Configuration conf = new Configuration();
		conf.set("startDate", args[0]);
		conf.set("endDate", args[1]);
		conf.set("selectionType", selectionType);
		conf.set("informationType", informationType);

		try {
			// Cria o job a ser executado
			Job job = Job.getInstance(conf, "dataweather");
			job.setJarByClass(Main.class);

			job.setMapperClass(WeatherMapper.class);
			// job.setCombinerClass(StatisticReducer.class);

			job.setReducerClass(StatisticReducer.class);

			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);

			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(DoubleWritable.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			// Cria uma instancia do sistema de arquivos para podemos consultar
			// os arquivos
			FileSystem hdfs = FileSystem.get(conf);

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
				if (hdfs.exists(new Path(path)))
					FileInputFormat.addInputPath(job, new Path(path));
				firstYearI++;
			}

			// Usando output como /user/<usuario>/output
			Path output = new Path("output");
			// Usando output como argumento
			/* Path output = new Path(args[3]); */

			FileOutputFormat.setOutputPath(job, output);

			System.out.println("Deletando a pasta output se ela ja existir");
			// Checa se a pasta de output ja existe, e se existir deleta a mesma
			if (hdfs.exists(output))
				hdfs.delete(output, true);

			System.out.println("Input/Output foi");

			if (job.waitForCompletion(true)) {
				getAverageList(hdfs);
				System.out.println("acabou o job");
				// System.exit(0);
			} else {
				System.out.println("fim");
				System.exit(1);
			}
		} catch (IOException e) {
			System.out.println("Não foi possível criar o job");
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fim");

		// CODIGO PARA TESTAR O GRAFICO
		// List<Double> x = new ArrayList<>();
		// List<Double> y = new ArrayList<>();
		// for (int i = 0; i < 10; i++) {
		// x.add((double) i);
		// x.add((double) (i + 3));
		// }
		// LineChart chart = new LineChart(x, y);
	}

	public static ArrayList<Tuple> getAverageList(FileSystem hdfs) throws IOException {
		Path path = new Path("output/part-r-00000");
		BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(path)));
		ArrayList<Tuple> list = new ArrayList<Tuple>();
		String line = reader.readLine();
		while (line != null) {
			String[] splitLine = line.split(" ");
			if (splitLine[0] == "avg")
				list.add(new Tuple(splitLine[1], Double.valueOf(splitLine[2])));
			line = reader.readLine();
		}
		;
		return list;
	}
}
