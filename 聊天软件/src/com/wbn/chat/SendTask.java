package com.wbn.chat;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SendTask implements Runnable {

	Socket socket;
	public SendTask(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			OutputStream out = socket.getOutputStream();
			Scanner sc = new Scanner(System.in);
			
			System.out.println("输入：");
			String msg;
			while(true) {
				//发送目标ID和消息
				msg = sc.nextLine();
				out.write(msg.getBytes("UTF-8"));
				out.flush();				
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
