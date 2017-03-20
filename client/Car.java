package client;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import gas_station.Cashier;
import gas_station.GasStation;
import gas_station.GasStation.FuelType;

public class Car extends Thread{

	private FuelType fuel;
	private int ammount;
	private int column;
	
	private void setFuel(FuelType fuel) {
		this.fuel = fuel;
	}
	
	private void setAmmount(int ammount) {
		this.ammount = ammount;
	}
	
	private void setColumn(int column) {
		this.column = column;
	}
	
	public int getColumn() {
		return column;
	}
	
	public FuelType getFuel() {
		return fuel;
	}
	
	public int getAmmount() {
		return ammount;
	}
	
	@Override
	public void run() {
		int rndColumn = new Random().nextInt(GasStation.columns.size());
		this.setColumn(rndColumn);
		ArrayBlockingQueue<Car> column = GasStation.columns.get(rndColumn);
		try {
			column.put(this);
		} catch (InterruptedException e) {
			System.out.println("Interrupted car.");
		}
	}
	
	public void pumpFuel() {
		int rnd = new Random().nextInt(100);
		int ammount = new Random().nextInt(30)+11;
		if(rnd <= 33) {
			this.setFuel(FuelType.DIESEL);
			this.setAmmount(ammount);
		}
		if(rnd > 33 && rnd <= 66) {
			this.setFuel(FuelType.GAS);
			this.setAmmount(ammount);
		}
		if(rnd > 66) {
			this.setFuel(FuelType.GASOLINE);
			this.setAmmount(ammount);
		}
		payBill();
	}
	
	public synchronized void payBill() {
		if(new Random().nextBoolean()) {
			try {
				GasStation.getCashier1().getCustomres().put(this);
			} catch (InterruptedException e) {
				System.out.println("Interrupted pay bill.");
			}
		} else {
			try {
				GasStation.getCashier2().getCustomres().put(this);
			} catch (InterruptedException e) {
				System.out.println("Interrupted pay bill.");
			}
		}
		
	}
}
