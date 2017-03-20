package gas_station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import client.Car;

public class GasStation {
	
	public enum FuelType{DIESEL, GAS, GASOLINE};
	
	public static ArrayList<ArrayBlockingQueue> columns = new ArrayList<ArrayBlockingQueue>();
	private static Cashier cashier1 = new Cashier("Minka");
	private static Cashier cashier2 = new Cashier("Ganka");
	private FuelBoy fuelBoy1 = new FuelBoy("Niki");
	private FuelBoy fuelBoy2 = new FuelBoy("Krasi");

	public GasStation() {
		for(int i = 0; i < 5; i++) {
			ArrayBlockingQueue<Car> column = new ArrayBlockingQueue<Car>(10);
			columns.add(column);
		}
		fuelBoy1.start();
		fuelBoy2.start();
		cashier1.start();
		cashier2.start();
	}
	
	public static Cashier getCashier1() {
		return cashier1;
	}
	
	public static Cashier getCashier2() {
		return cashier2;
	}
	
	public List<ArrayBlockingQueue> getColumns() {
		return Collections.unmodifiableList(columns);
	}
	
	public void startDay() {
		while(true) {
			try {
				Thread.sleep(15*1000);
			} catch (InterruptedException e) {
				System.out.println("Interrupted ");
			}
		}
	}
}
