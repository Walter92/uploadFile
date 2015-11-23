package cn.edu.uestc.demo.socketUpload;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import org.junit.Test;

public class UploadClient implements Client {
	private User user;
	private BufferedReader br, socketBr;
	private ObjectOutputStream oos;
	private PrintWriter pw;
	private OutputStream os;
	private BufferedOutputStream bos;
	private Socket socket;
	private String msg;

	public UploadClient() throws Exception {
		//新建用户对象
		user = new User();
		//获取键盘输入流
		br = new BufferedReader(new InputStreamReader(System.in));
		//链接服务器的socket
		socket = new Socket("localhost", 9527);
		//获取socket输出流
		os = socket.getOutputStream();
		socketBr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//将输出流包装为各种包装流方便发送不同类型数据
		pw = new PrintWriter(new OutputStreamWriter(os), true);
		oos = new ObjectOutputStream(os);
		bos = new BufferedOutputStream(os);
		//执行客户端入口程序
		welcome();
	}

	public static void main(String[] args) throws Exception {
		new UploadClient();
	}

	// 获取用户输入信息
	public String getInput() {
		try {
			String msg = br.readLine();
			return msg;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * 欢迎
	 */
	@Test
	public void welcome() {
		while (true) {
			try {
				System.out.println("-----欢迎使用本系统，请输入：----");
				System.out.println("0：注册\n1:登录\n2:退出");
				switch (getInput()) {
				case "0":// 注册
					register();
					break;
				case "1":// 登录
					login();
					break;
				case "2":// 退出客户端
					quit();
				default:// 输入有误，请重新输入
					System.out.println("请输入0，1，2.");
					continue;
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 用户注册
	 */
	@Test
	public void register() {
		try {
			while (true) {
				System.out.print("请输入用户名:");
				String name = getInput();
				System.out.print("请输入密码：");
				String passwd = getInput();
				System.out.print("请再次输入密码：");
				if (!passwd.equals(getInput())) {
					System.out.println("两次输入密码不同，请重新输入！");
					continue;
				}
				user.setName(name);
				user.setPasswd(passwd);
				// System.out.println(Arrays.toString("register".getBytes("utf8")));
				//将用户输入的用户名和密码设置号后通过对象序列化发送到服务端
				oos.writeObject(user);
				oos.flush();
				//发送注册信息
				pw.println(Server.REGISTER);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void upload() {
		System.out.println("登录成功！请输入你要上传的文件路径(限制大小1M)：");
		BufferedInputStream bis = null;
		while (true) {
			String path = getInput();
			File file = new File(path);
			if (!file.exists() || !file.isFile() || !(file.length() < Client.MAX_SIZE)) {
				System.out.println("请输入一个可用的文件路径(限制大小1M):");
				continue;
			}
			try {
				bis = new BufferedInputStream(new FileInputStream(file));
				byte[] buf = new byte[1024];
				int num = 0;
				bos.write(buf, 0, num);
				bos.flush();
				System.out.println("上传完毕！");
				break;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					if (bis != null)
						bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 登录系统
	public void login() {
		System.out.print("请输入用户名:");
		String name = getInput();
		System.out.print("请输入密码：");
		String passwd = getInput();
		user.setName(name);
		user.setPasswd(passwd);

		try {
			oos.writeObject(user);
			oos.flush();
			pw.println(Server.LOGIN);
			System.out.println("成功发送登录信息。");
			boolean flag = new Boolean(socketBr.readLine());
			// System.out.println(flag);
			System.out.println("开始判断是否登录成功。");
			if (flag) {
				upload();
				return;
			}
			System.out.println("登录失败，请检查用户名和密码，重试。");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 退出客户端
	public void quit() {
		System.out.println("谢谢使用本系统，再见！");
		try {
			if (br != null)
				br.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(Client.EXIT);
	}
}
