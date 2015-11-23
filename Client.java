package cn.edu.uestc.demo.socketUpload;

public interface Client {
	public static final int EXIT=0;
	
	long MAX_SIZE=1024*1024;
	
	void login();
	void register();
	void quit();
	

}
