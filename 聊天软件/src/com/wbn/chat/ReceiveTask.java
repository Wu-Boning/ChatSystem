package com.wbn.chat;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ReceiveTask implements Runnable {

	Socket socket;
	
	public ReceiveTask(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			InputStream in = socket.getInputStream();
			byte []buf = new byte[256];
			int size;
			String msg;
			while(true) {
				size = in.read(buf);
				msg = new String(buf, 0, size, "UTF-8");
				System.out.println(msg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
