package gas_station;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
		int counter = 0;
		File report = new File("report getSumOfAllFuel"+(++counter)+"-"+LocalDate.now()+".txt");
		try {
			report.createNewFile();
		} catch (IOException e1) {
			System.out.println("Error creating file.");
		}
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
			try(FileOutputStream fos = new FileOutputStream(report)){
				while(r.next()) {
					finalResult += r.getInt("sum gasoline");
				}
				while(r1.next()) {
					finalResult += r1.getInt("sum diesel");
				}
				while(r2.next()) {
					finalResult += r2.getInt("sum gas");
				}
				String s = "Total sum - " + finalResult;
				System.out.println(s);
				for(char c : s.toCharArray()) {
					fos.write(c);
				}
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			
		} catch (SQLException e) {
			System.out.println("Prepare statement error " + e.getMessage());
		}
	}

	private void getSumByFuelType(Connection connection) {
		int counter = 0;
		File report = new File("report getSumByFuelType"+(++counter)+"-"+LocalDate.now()+".txt");
		try {
			report.createNewFile();
		} catch (IOException e1) {
			System.out.println("Error creating file " + e1.getMessage());
		}
		try {
			String st = "SELECT fuel_type, SUM(fuel_quantity) AS 'sum' FROM station_loadings GROUP BY fuel_type;";
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(st);
			StringBuilder sb = new StringBuilder();
			while(result.next()) {
				String s = result.getString("fuel_type") + " - " + result.getInt("sum");
				System.out.println(s);
				sb.append(s + " ");
				try(FileOutputStream fos = new FileOutputStream(report)) {
					String str = sb.toString();
					for(char c : str.toCharArray()) {
						fos.write(c);
					}
				} catch (FileNotFoundException e) {
					System.out.println(e.getMessage());
				} catch (IOException e) {
					e.getMessage();
				}
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
			StringBuilder sb = new StringBuilder();
			while(result.next()) {
				String result1 = "Kolonka: " + result.getInt("kolonka_id");
				String result2 = "Number of cars: " + result.getInt("kolonka");
				sb.append(result1 + " " + result2 + " ");
				System.out.println(result1);
				System.out.println(result2);
				System.out.println("===================");
			}
			int counter = 0;
			File report = new File("report-getNumberOfCarsForEachColumn"+(++counter)+"-"+LocalDate.now()+".txt");
			try {
				report.createNewFile();
			} catch (IOException e1) {
				System.out.println("Error creating file " + e1.getMessage());
			}
			try(FileOutputStream fos = new FileOutputStream(report)) {
				String s = sb.toString();
				for(char c : s.toCharArray()) {
					fos.write(c);
				}
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
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
			StringBuilder sb = new StringBuilder();
			while(result.next()) {
				String s1 = "Kolonka- " + result.getInt("kolonka_id");
				String s2 = "Fuel type- " + result.getString("fuel_type");
				String s3 = "Fuel quantity- " + result.getInt("fuel_quantity");
				String s4 = "Loading time- " + result.getString("loading_time");
				sb.append(s1 + " ");
				sb.append(s2 + " ");
				sb.append(s3 + " ");
				sb.append(s4 + " ");
				System.out.println(s1);
				System.out.println(s2);
				System.out.println(s3);
				System.out.println(s4);
				System.out.println("===================");
			}
			int counter = 0;
			File report = new File("report-listAllLoadings"+(++counter)+"-"+LocalDate.now()+".txt");
			try {
				report.createNewFile();
				try(FileOutputStream fos = new FileOutputStream(report)) {
					String s = sb.toString();
					for(char c : s.toCharArray()) {
						fos.write(c);
					}
				}
			} catch (IOException e1) {
				System.out.println("Error creating file " + e1.getMessage());
			}
		} catch (SQLException e) {
			System.out.println("Error in select statement." + e.getMessage());
		}
	}
}
