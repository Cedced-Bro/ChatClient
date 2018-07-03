package server.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Cedric
 * @version 1.0
 * @category network.database
 * @description This class allows an easy use of Databases
 */
public class DatabaseController {

	// **********
	// * Fields *
	// **********
	private int port;
	private String host;
	private String usr;
	private String pwd;
	private String databaseName;
	private Connection con;


	// ****************
	// * Constructors *
	// ****************

	/**
	 * Prepares a Connection to a Database by saving the Database-Credentials.
	 *
	 * @param host the host for the database
	 * @param port the port, on which the database runs
	 * @param usr the username for the database
	 * @param pwd the password for the database
	 * @param databaseName the database name, to which the connection is needed
	 */
	public DatabaseController(String host, int port, String usr, String pwd, String databaseName) {
		this.host = host;
		this.port = port;
		this.usr = usr;
		this.pwd = pwd;
		this.databaseName = databaseName;
	}

	/**
	 * Prepares a Connection to a Database by saving the Database-Credentials.
	 * If setConnection is true it will also try to connect.
	 *
	 * @param host the host for the database (db)
	 * @param port the port, on which the db runs
	 * @param usr the username for the db
	 * @param pwd the password for the db
	 * @param databaseName the db name, to which the connection is needed
	 * @param setConnection whether also a new connection should be established
	 * @throws SQLException This exception gets thrown if an error occurs while connecting to the db
	 * @throws ClassNotFoundException Gets thrown if SQL-Driver couldn't be loaded
	 * @throws IllegalAccessException Gets thrown if SQL-Driver couldn't be loaded
	 * @throws InstantiationException Gets thrown if SQL-Driver couldn't be loaded
	 */
	public DatabaseController(String host, int port, String usr, String pwd, String databaseName, boolean setConnection) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		this(host, port, usr, pwd, databaseName);
		if (setConnection) connect();
	}

	// *******************
	// * Private Methods *
	// *******************

	/**
	 * Internal method for connecting to the Database.
	 *
	 * @throws SQLException This Exception gets thrown if an error occurs while connecting to the db
	 * @throws ClassNotFoundException Gets thrown if SQL-Driver couldn't be loaded
	 * @throws IllegalAccessException Gets thrown if SQL-Driver couldn't be loaded
	 * @throws InstantiationException Gets thrown if SQL-Driver couldn't be loaded
	 */
	private void connect() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		// TODO: Put this line into own thread which stops connecting after 20 seconds --> timeouterror
		con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?verifyServerCertificate=false&useSSL=true&requireSSL=true", usr, pwd);
	}

	// ******************
	// * Public Methods *
	// ******************

	/**
	 * Executes given SQL-queries and returns the result.
	 *
	 * @param query The SQL-query which gets executed
	 * @return the result of that SQL-query
	 * @throws SQLException Gets thrown if an error occurs while executing the query
	 * @throws ClassNotFoundException Gets thrown if SQL-Driver couldn't be loaded
	 * @throws IllegalAccessException Gets thrown if SQL-Driver couldn't be loaded
	 * @throws InstantiationException Gets thrown if SQL-Driver couldn't be loaded
	 */
	public ResultSet executeSQL(String query) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (!isConnected()) connect();
		Statement st = con.createStatement();
		return st.executeQuery(query);
	}

	/**
	 * Checks if there is valid connection to the Database.
	 *
	 * @return whether there is a valid connection to the database
	 * @throws SQLException Gets thrown if an error occurs while checking
	 */
	public boolean isConnected() throws SQLException {
		return !(con.isClosed());
	}

	/**
	 * Closes the connection to the Database.
	 *
	 * @throws SQLException Gets throws if an error occurs while disconnecting
	 */
	public void disconnect() throws SQLException {
		con.close();
	}

}
