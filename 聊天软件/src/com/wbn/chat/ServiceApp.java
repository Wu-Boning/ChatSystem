package com.wbn.chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceApp {

	/**
	 * 用户列表
	 */
	HashMap<String, User> usersList;

	ServerSocket serverSocket;
	
	int port = 9001;
	
	InetAddress address;

	ExecutorService pool;

	public ServiceApp() {
		super();
		usersList = new HashMap<>();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("服务器启动.....");
	}

	public void start() {
		pool = Executors.newCachedThreadPool();
		try {
			while (true) {
				Socket socket = serverSocket.accept();

				pool.execute(new OnlineService(socket, usersList));

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ServiceApp().start();
	}


}
