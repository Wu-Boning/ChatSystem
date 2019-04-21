package com.wbn.chat;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class User {

	String id;
	
	String nick;
	
	Socket socket;
	
	boolean isOnline;
	
	ArrayList<String> msgList = new ArrayList<>();

	
	
	public User() {
		super();
	}

	public User(String id, String nick, Socket socket, boolean isOnline, ArrayList<String> msgList) {
		super();
		this.id = id;
		this.nick = nick;
		this.socket = socket;
		this.isOnline = isOnline;
		this.msgList = msgList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public ArrayList<String> getMsgList() {
		return msgList;
	}

	public void setMsgList(ArrayList<String> msgList) {
		this.msgList = msgList;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void print() {
		System.out.print(id + "，" + nick + "，" +
				socket.getInetAddress() + "，" + socket.getPort() + "，" +
				isOnline);
	}
	
}
