package gas_station;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import client.Car;

public class GasStation {

	public enum FuelType {
		DIESEL, GAS, GASOLINE
	};

	public static ArrayList<ArrayBlockingQueue> columns = new ArrayList<ArrayBlockingQueue>();
	private static Cashier cashier1 = new Cashier("Minka");
	private static Cashier cashier2 = new Cashier("Ganka");
	private FuelBoy fuelBoy1 = new FuelBoy("Niki");
	private FuelBoy fuelBoy2 = new FuelBoy("Krasi");

	public GasStation() {
		for (int i = 0; i < 5; i++) {
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
		Connector connector = Connector.getInstance();
		Connection connection = connector.getConnection();
		while (true) {
			try {
				Thread.sleep(15 * 1000);
			} catch (InterruptedException e) {
				System.out.println("Interrupted day.");
			}
			try {
				listAllLoadings(connection);
				Thread.sleep(1*1000);
				getNumberOfCarsForEachColumn(connection);
				Thread.sleep(1*1000);
				getSumByFuelType(connection);
				Thread.sleep(1*1000);	
				getSumOfAllFuel(connection);
			} catch (InterruptedException e) {
				System.out.println("Interrupted while doing the 24 hour round up.");
			}
			
		}
	}
	
	private void getSumOfAllFuel(Connection connection) {
		String st1 = "SELECT SUM(fuel_quantity)*2 AS 'sum gasoline' FROM station_loadings WHERE fuel_type='Gasoline';";
		String st2 = "SELECT SUM(fuel_quantity)*2.40 AS 'sum diesel' FROM station_loadings WHERE fuel_type='Diesel';";
		String st3 = "SELECT SUM(fuel_quantity)*1.60 AS 'sum gas' FROM station_loadings WHERE fuel_type='Gas';";
		try {
			Statement statement1 = connection.createStatement();
			ResultSet r = statement1.executeQuery(st1);
			Statement statement2 = connection.createStatement();
			ResultSet r1 = statement2.executeQuery(st2);
			Statement statement3 = connection.createStatement();
			ResultSet r2 = statement3.executeQuery(st3);
			int finalResult = 0;
			while(r.next()) {
				finalResult += r.getInt("sum gasoline");
			}
			while(r1.next()) {
				finalResult += r1.getInt("sum diesel");
			}
			while(r2.next()) {
				finalResult += r2.getInt("sum gas");
			}
			System.out.println("Total sum - " + finalResult);
			
		} catch (SQLException e) {
			System.out.println("Prepare statement error " + e.getMessage());
		}
	}

	private void getSumByFuelType(Connection connection) {
		try {
			String st = "SELECT fuel_type, SUM(fuel_quantity) AS 'sum' FROM station_loadings GROUP BY fuel_type;";
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(st);
			while(result.next()) {
				System.out.println(result.getString("fuel_type") + " - " + result.getInt("sum"));
			}
		} catch (SQLException e) {
			System.out.println("Error creating statement." + e.getMessage());
		}
		
	}

	public void getNumberOfCarsForEachColumn(Connection connection) {
		String st = "SELECT COUNT(*) AS 'kolonka', kolonka_id FROM station_loadings GROUP BY kolonka_id ORDER BY kolonka_id;";
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(st);
			while(result.next()) {
				System.out.println("Kolonka: " + result.getInt("kolonka_id"));
				System.out.println("Number of cars: " + result.getInt("kolonka"));
				System.out.println("===================");
			}
		} catch (SQLException e) {
			System.out.println("Error creating statement." + e.getMessage());
		}
	}

	public void listAllLoadings(Connection connection) {
		String st = "SELECT * FROM station_loadings GROUP BY kolonka_id ORDER BY kolonka_id, loading_time;";
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(st);
			while(result.next()) {
				System.out.println("Kolonka- " + result.getInt("kolonka_id"));
				System.out.println("Fuel type- " + result.getString("fuel_type"));
				System.out.println("Fuel quantity- " + result.getInt("fuel_quantity"));
				System.out.println("Loading time- " + result.getString("loading_time"));
				System.out.println("===================");
			}
		} catch (SQLException e) {
			System.out.println("Error in select statement." + e.getMessage());
		}
	}
}
