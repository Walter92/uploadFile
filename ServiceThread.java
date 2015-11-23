package cn.edu.uestc.demo.socketUpload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

//为客户端服务线程
public class ServiceThread implements Runnable {
	// 需要服务的用户对象和socket
	User user;
	Socket socket;

	// 利用socket构造并启动
	public ServiceThread(Socket socket) {
		this.socket = socket;
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			//获取socket输入输出流，并且包装为字符流，输出流自动刷新
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			String info = null;
			try {
				//创建对象输入流，接收客户端发送来的对象
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				//将接收到的对象强转为user
				user = (User) ois.readObject();
				// System.out.println(user.getName() + ":" + user.getPasswd());
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			//登录是否成功返回标志
			boolean flag = false;
			while ((info = br.readLine()) != null) {
				switch (info) {
				case "0":
					UploadServer.doRegister(user);
					break;
				case "1":
					flag = UploadServer.dologin(user);
					break;
				default:
					break;

				}
				if (flag) {
					//如果登录成功，给客户端返回true
					System.out.println("查询成功。");
					pw.println("true");
					//登录成功后跳转到文件上传服务
					UploadServer.doUpLoad(user, socket);
				}
			}
			// 获取客户端是登录或者注册信息
			// System.out.println(info);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(socket.getLocalAddress().getHostName() + "下线");
			try {
				if (socket != null)
					socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}

