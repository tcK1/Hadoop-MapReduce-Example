public class Tuple {

	private int date;
	private double avg;
	private double dev;

	public Tuple(int date, double avg, double dev) {
		this.date = date;
		this.avg = avg;
		this.dev = dev;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public double getDev() {
		return dev;
	}

	public void setDev(double dev) {
		this.dev = dev;
	}

}
