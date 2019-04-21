package com.wbn.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;

public class ClientApp {
	
	int port =9001;
	
	Socket socket;
	
	String id = "10002";
		
	Thread sender;
	
	Thread receiver;
	
	public ClientApp() {
		try {
			socket = new Socket("127.0.0.1", port);
			
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();			
			
			//发送ID
			out.write(id.getBytes("UTF-8"));
			out.flush();
			
			//发送昵称
			Scanner sc = new Scanner(System.in);
			System.out.print("昵称：");
			String nick = sc.nextLine();
			out.write(nick.getBytes("UTF-8"));
			out.flush();			
			
			//接收用户在线情况
			byte[] buf = new byte[1024];
			int size = in.read(buf);
			String userMsg = new String(buf, 0, size, "UTF-8");
			System.out.println(userMsg);
			System.out.println("-----------------------------");
			
			//接受离线消息
			size = in.read(buf);
			String msg = new String(buf, 0, size, "UTF-8");
			ArrayList<String> msgList = new Gson().fromJson(msg, ArrayList.class);
			if(msgList.size() == 0)
				System.out.println("离线消息：无");
			else
				System.out.println("离线消息：");
			for (String string : msgList) {
				System.out.println(string);
			}	
			System.out.println("-----------------------------");
			
			sender = new Thread(new SendTask(socket));
			receiver = new Thread(new ReceiveTask(socket));
			
			System.out.println("##ID：内容##");
			sender.start();
			receiver.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public static void main(String[] args) {
		new ClientApp();
	}

}
