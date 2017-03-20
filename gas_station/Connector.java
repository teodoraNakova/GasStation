package gas_station;

import gas_station.GasStation.FuelType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Connector {

	private static final String DB_IP = "localhost";
	private static final String DB_PORT = "3306";
	private static final String DB_NAME = "hr";
	private static final String DB_USER = "tedi";
	private static final String DB_PASS = "sudjunka";
	private static Connector connector;
	private static Connection connection;
	
	private Connector() {
		createConnection();
	}
	
	public static Connector getInstance() {
		if(connector == null) {
			connector = new Connector();
		}
		return connector;
	}
	
	public synchronized void addToDataBase(int column, FuelType fuel, int ammount, LocalDate date) {
		
			String fuelType = fuel.toString();
			String localDate = date.toString();
			PreparedStatement statement;
			try {
				statement = connection.prepareStatement("INSERT INTO station_loadings(kolonka_id, fuel_type, fuel_quantity, loading_time) "
						+ "VALUES(?, ?, ?, ?)");
				statement.setLong(1, column);
				statement.setString(2, fuelType);
				statement.setLong(3, ammount);
				statement.setString(4, localDate);
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Error creating statement.");
			}
		
	}

	private static void createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found");
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"+DB_IP+":"+DB_PORT+"/"+DB_NAME,DB_USER, DB_PASS);
		} catch (SQLException e) {
			System.out.println("Error connecting to Database");
		}
		
	}

}
