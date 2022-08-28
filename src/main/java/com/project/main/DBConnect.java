package com.project.main;

import java.sql.*;

public class DBConnect
{
	private static final String URL = "jdbc:mysql://CCC-mysql";
	private static final String DATABASE = "test";
	private static final int PORT = 3306;
	private static final String UNAME = "root";
	private static final String PASSWORD = "pass";

	public static Connection getConnection() throws SQLException, ClassNotFoundException
        {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(URL + ":" + PORT + "/" + DATABASE + "?characterEncoding=UTF-8", UNAME, PASSWORD);
	}
}