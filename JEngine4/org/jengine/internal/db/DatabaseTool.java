package org.jengine.internal.db;

import java.sql.*;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/**
 * This class is used in a Database MBean in order to simplify the script code that connects
 * to a Database.
 * 
 * @author jaz
 *
 */
public class DatabaseTool
{
	private Logger log;

	private DataSource dataSource;
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;

	private String driver;
	private String connectionString;
	private String username;
	private String password;

	public DatabaseTool(String driver, String connStr, String user, String pw)
	{
		log = Logger.getLogger(this.getClass());
		setDriver(driver);
		setConnectionString(connStr);
		setUsername(user);
		setPassword(pw);
		initDBConnection();
	}

	private void initDBConnection()
	{
		try
		{
			Class.forName(driver);
		}
		catch (ClassNotFoundException e)
		{
			log.error("Database JDBC Driver ClassNotFoundException : " + e.getMessage());
		}
		try
		{
			connection = DriverManager.getConnection(connectionString,username,password);
		}
		catch (SQLException e)
		{
			log.error("SQLException : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void cleanup()
	{
		try { connection.close(); }
		catch (SQLException e) { log.error("SQLException : " + e.getMessage()); }
	}

	public void runSQL(String sqlString)
	{
		Statement stmt = null;
		try
		{
			stmt = connection.createStatement();
			stmt.execute(sqlString);
		}
		catch (SQLException se)
		{
			log.error("SQLException : " + sqlString);
			log.error(se.getMessage());
		}
		finally
		{
			try
			{
				stmt.close();
			}
			catch (SQLException se)
			{
				log.error("SQLException : " + sqlString);
				log.error(se.getMessage());
			}
		}
                                                                        

	}

	private void setDriver(String driver)
	{
		this.driver = driver;
	}
	private void setConnectionString(String connStr)
	{
		this.connectionString = connStr;
	}
	private void setUsername(String user)
	{
		this.username = user;
	}
	private void setPassword(String pw)
	{
		this.password = pw;
	}

}

