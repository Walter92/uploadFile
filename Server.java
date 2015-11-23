package cn.edu.uestc.demo.socketUpload;

import java.net.Socket;

public interface Server {
	int REGISTER=0;
	int LOGIN=1;
	void doRegister(User user);
	void doUpLoad(User user, Socket socket);
	boolean dologin(User user);
}
