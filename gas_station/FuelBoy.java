package gas_station;

import java.util.concurrent.ArrayBlockingQueue;

import client.Car;

public class FuelBoy extends Thread {

	private String name;

	public FuelBoy(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		while (true) {
			for (ArrayBlockingQueue<Car> column : GasStation.columns) {
				if (column.size() != 0) {
					try {
						Car car = column.take();
						Thread.sleep(5 * 1000);
						car.pumpFuel();
						System.out.println("fuel boy did his job.");
					} catch (InterruptedException e) {
						System.out.println("Interrupted fuel boy.");
					}
				}
			}
		}
	}
}
