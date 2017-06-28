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
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, DoubleWritable>.Context context) throws IOException, InterruptedException {

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
		}
		catch(ParseException e) {
			System.err.println("Data no formato invalido ");
			e.printStackTrace();
		}

		// se tudo certo ate agora, começa as contas

	}
}