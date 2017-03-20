package gas_station;

import gas_station.GasStation.FuelType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import client.Car;

public class Cashier extends Thread {

	private String name;
	private ArrayBlockingQueue<Car> customres = new ArrayBlockingQueue<Car>(10);
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<FuelType, ConcurrentHashMap<LocalDate, ArrayList<Integer>>>> register = 
			new ConcurrentHashMap<Integer, ConcurrentHashMap<FuelType, ConcurrentHashMap<LocalDate, ArrayList<Integer>>>>(); // column, fuel, date, ammount
	public Cashier(String name) {
		this.name = name;
	}

	public ArrayBlockingQueue<Car> getCustomres() {
		return customres;
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (this.customres.size() > 0) {
					Car car = this.customres.peek();
					Thread.sleep(5 * 1000);
					int column = car.getColumn();
					FuelType fuel = car.getFuel();
					int ammount = car.getAmmount();
					LocalDate date = LocalDate.now();
					if (!register.containsKey(column)) {
						register.put(
								column,
								new ConcurrentHashMap<FuelType, ConcurrentHashMap<LocalDate, ArrayList<Integer>>>());
						if (!register.get(column).containsKey(fuel)) {
							register.get(column)
									.put(fuel,
											new ConcurrentHashMap<LocalDate, ArrayList<Integer>>());
						}
						if (!register.get(column).get(fuel).containsKey(date)) {
							register.get(column).get(fuel)
									.put(date, new ArrayList<Integer>());
						}
					}
					register.get(column).get(fuel).get(date).add(ammount);
					System.out.println("Success");
					this.customres.take();
				}
			} catch (InterruptedException e) {
				System.out.println("Interrupted cashier.");
			}

		}
	}
}
