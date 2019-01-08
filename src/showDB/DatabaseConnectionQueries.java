package showDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DatabaseConnectionQueries {
	public static Double[] getAvgFromDB(String mapName)
	{

		HashMap<String, Long> mapFileHashes = new HashMap<>();
		mapFileHashes.put("Ex4_OOP_example1.csv", 2128259830L);
		mapFileHashes.put("Ex4_OOP_example2.csv", 1149748017L);
		mapFileHashes.put("Ex4_OOP_example3.csv", -683317070L);
		mapFileHashes.put("Ex4_OOP_example4.csv", 1193961129L);
		mapFileHashes.put("Ex4_OOP_example5.csv", 1577914705L);
		mapFileHashes.put("Ex4_OOP_example6.csv", -1315066918L);
		mapFileHashes.put("Ex4_OOP_example7.csv", -1377331871L);
		mapFileHashes.put("Ex4_OOP_example8.csv", 306711633L);
		mapFileHashes.put("Ex4_OOP_example9.csv", 919248096L);
		Long hashOfFile = mapFileHashes.get(mapName);

		System.out.println("Will query map ID: "+ hashOfFile); //todo: delete

		String jdbcUrl="jdbc:mysql://ariel-oop.xyz:3306/oop"; //oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
		String jdbcUser="student";
		String jdbcPassword="student";
		Double[] avgs = new Double[4]; //first is our AVG, second is total AVG for the map.
		//third is maxScore for Liad&Timor in this map , 4th is maxScore for this map,
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection =
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);

			Statement statement = connection.createStatement();

			//get data
			String allCustomersQuery = "SELECT * FROM logs WHERE FirstID='316602630' AND SomeDouble='" +hashOfFile+ "';";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			double totalPts = 0;
			int count = 0;
			double maxScore = Double.MIN_VALUE;
			double currentScore;
			while(resultSet.next()){
				currentScore = resultSet.getDouble("Point");
				totalPts += currentScore;
				count++;
				if(currentScore > maxScore){
					maxScore = currentScore;
				}


			}
			System.out.println("Total Pts : " + totalPts); //todo: delete
			System.out.println("Count : " + count); //todo: delete
			System.out.println("Max Score for Liad & Timor: " + maxScore);
			double avg = totalPts/count;
			avgs[0] = avg;
			avgs[2] = maxScore;

			//now get the average for this map excluding our score.
			allCustomersQuery = "SELECT * FROM logs WHERE SomeDouble='" +hashOfFile+ "' AND (NOT FirstID='316602630');";
			resultSet = statement.executeQuery(allCustomersQuery);
			totalPts = 0;
			count = 0;
			maxScore = Double.MIN_VALUE;
			while(resultSet.next()){
				currentScore = resultSet.getDouble("Point");
				totalPts += currentScore;
				count++;
				if(currentScore > maxScore){
					maxScore = currentScore;
				}
			}
			avg = totalPts/count;
			avgs[1] = avg;
			avgs[3] = maxScore;

			resultSet.close();
			statement.close();
			connection.close();
		}

		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return avgs;
	}

}
