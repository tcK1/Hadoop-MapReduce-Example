
import java.io.IOException;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main {

	static String startDate;

	static String endDate;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.println("Como deseja que os dados sejam agrupados?");
		System.out.println("Ano-Mes-Dia: T"); // Tudo
		System.out.println("Ano-Mes: P"); // Parcial
		System.out.println("Ano: A"); // Ano
		System.out.println("Mes: M"); // Mes
		System.out.println("Dia: D"); // Dia
		String selectionType = sc.nextLine().toUpperCase();

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
		String informationType = sc.nextLine().toUpperCase();

		sc.close();

		Configuration conf = new Configuration();
		conf.set("startDate", args[0]);
		conf.set("endDate", args[1]);
		conf.set("selectionType", selectionType);
		conf.set("informationType", informationType);
		try {
			Job job = Job.getInstance(conf, "dataweather");
			job.setJarByClass(Main.class);

			FileInputFormat.addInputPath(job, new Path(args[2]));
			FileOutputFormat.setOutputPath(job, new Path(args[3]));

			if (job.waitForCompletion(true)) {

				System.exit(0);
			} else {

				System.exit(1);
			}
		} catch (IOException e) {
			System.out.println("Não foi possível criar o job");
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
