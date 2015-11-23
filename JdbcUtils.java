package cn.edu.uestc.demo.socketUpload;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

public class JdbcUtils {
	private static Connection conn = null;

	static {
		try {
			InputStream in = JdbcUtils.class.getResourceAsStream("dbconfig.properties");
			Properties props = new Properties();
			props.load(in);
			String dirverclass = props.getProperty("driverClassName");
			String url = props.getProperty("url");
			String user = props.getProperty("username");
			String passwd = props.getProperty("passwd");
			Class.forName(dirverclass);
			conn = DriverManager.getConnection(url, user, passwd);
		} catch (Exception e) {
		}
	}

	public static Connection getConnection() {
		return conn;
	}

}
