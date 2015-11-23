package cn.edu.uestc.demo.socketUpload;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

public class UploadServer {

	private static Connection conn = JdbcUtils.getConnection();

	// 处理注册
	public static void doRegister(User user) {
		// 创建更新数据库sql语句
		String sql = "insert into tb_user(name,passwd) values(?,?)";
		PreparedStatement pstms = null;
		try {
			pstms = conn.prepareStatement(sql);
			pstms.setString(1, user.getName());
			pstms.setString(2, user.getPasswd());
			// 执行sql语句
			pstms.executeUpdate();
			System.out.println("新用户注册成功："+user.getName());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭资源
			if (pstms != null)
				try {
					pstms.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	// 处理文件上传
	public static void doUpLoad(User user, Socket socket) {
		// int maxid=getMaxIdOfFile(user);
		PreparedStatement pstmt = null;
		try {
			//获取socket流，准备接收文件字节数据
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			System.out.println("用户将上传文件。");
			byte[] buf=new byte[1024];
			int num = 0;
//			System.out.println(bis);
			//将用户上传的字节数据存入数组
			num=bis.read(buf);
			byte[] data=new byte[num];
			for(int i=0;i<num;i++)
				data[i]=buf[i];
			System.out.println("获取到用户上传数据。");
			//利用用户上传的字节数组创建一个Blob对象
			Blob blob = new SerialBlob(data);
			String sql = "insert user_files values(null,?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setBlob(1, blob);
			pstmt.setInt(2, user.getUid());
			//将用户数据和用户id作为外键存入数据库中
			pstmt.executeUpdate();
			System.out.println("成功将用户"+user.getName()+"的文件存入数据库！");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SerialException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	// private static int getMaxIdOfFile(User user){
	// String sql="select max(fid) from user_files where owner=?";
	//
	// }

	// 处理登录
	public static boolean dologin(User user) {
		// 创建查询sql语句
		System.out.println("用户登入本服务器。");
		//查询指定名字的用户id和密码
		String sql = "select uid,passwd from tb_user where name=?";
		PreparedStatement pstms = null;
		ResultSet rs = null;
		try {
			pstms = conn.prepareStatement(sql);
			pstms.setString(1, user.getName());
			rs = pstms.executeQuery();
			// 根据查询结果返回是否登录成功
			while (rs.next()) {
				//判断结果中密码是否和用户输入一致，一致则返回true，并给用户id设置为数据库中当初注册时自动生成的id
				if (user.getPasswd().equals(rs.getString(2))) {
					user.setUid(rs.getInt(1));
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭资源
			try {
				if (pstms != null)
					pstms.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// InputStream in=null;
		// System.out.println(conn);
		try {
			@SuppressWarnings("resource")
			// 启动服务器，监听9527端口
			ServerSocket serverSocket = new ServerSocket(9527);
			while (true) {
				System.out.println("服务器已经启动，等待链接......");
				Socket socket = serverSocket.accept();
				System.out.println(socket.getLocalAddress().getHostName() + "已经链接...");
				// 开启一个线程为接入的客户端服务
				new ServiceThread(socket);
				System.out.println("正在为" + socket.getLocalAddress().getHostName() + "服务中...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

