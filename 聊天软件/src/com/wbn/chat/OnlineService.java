package com.wbn.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;

public class OnlineService implements Runnable {
	

	/**
	 * 用户与目标用户套接字
	 */
	Socket socket1, socket2;
	
	/**
	 * 用户列表
	 */
	HashMap<String, User> usersList;
	
	/**
	 * 用户ID
	 */
	String id;

	/**
	 * 用户对象
	 */
	User user;
	
	final String errorMsg = "#输入ID格式不正确或没有找到该用户#";
	
	public OnlineService(Socket socket, HashMap<String, User> usersList) {
		super();
		this.socket1 = socket;
		this.usersList = usersList;
	}
	
	
	@Override
	public void run() {
		try {
			InputStream in = socket1.getInputStream();
			OutputStream out = socket1.getOutputStream();
			
			//接收用户（ID）
			receiveID(in);
			
			user.print();
			System.out.print(" 已连接");
			System.out.println();
			//发送用户在线信息（刷新）
			flushUsersList();
			
//			//发送用户在线信息
//			sendUsersMsg(out);
			
			//发送离线消息
			sendOutlineMsg(out);
			
			while(true) {
				//发送信息
				sendMsg(out, in);				
			}
			
		} catch (IOException e) {
			
		}finally {
			//用户离线后
			usersList.get(id).setOnline(false);//改变在线状态
			usersList.get(id).getMsgList().clear();//清空离线消息列表
			user.print();
			System.out.print(" 已断开");
			System.out.println();
			try {
				flushUsersList();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}


	/**
	 * 刷新每个用户的在线列表
	 * @throws IOException 
	 */
	private void flushUsersList() throws IOException {
		Socket socket;
		OutputStream out;
		for(String key : usersList.keySet()) {
			if(usersList.get(key).isOnline()) {
				socket = usersList.get(key).getSocket();
				out = socket.getOutputStream();
				sendUsersMsg(out);
			}
		}
	}


	/**
	 * 接收目标ID
	 * 将信息发送或存入离线消息列表
	 * @param out 
	 * @param in
	 * @throws IOException
	 */
	private void sendMsg(OutputStream out, InputStream in) throws IOException {
		int size;
		byte[] buf = new byte[256];
		boolean flag = false;
		//接收ID和信息
		String msg;
		String targetId = null;
		//判断输入格式是否正确
		do {
			size = in.read(buf);
			msg = new String(buf, 0, size, "utf-8");
			targetId = null;
			//区分":"和"："
			if(-1 == msg.indexOf("：")) {
				if(-1 == msg.indexOf(":")) {
					out.write(errorMsg.getBytes("utf-8"));
					out.flush();
					flag = true;
					continue;
				}else {
					targetId = msg.substring(0, msg.indexOf(":"));
					msg = msg.substring(msg.indexOf(":")+1);
				}
			}else {
				targetId = msg.substring(0, msg.indexOf("："));
				msg = msg.substring(msg.indexOf("：")+1);
			}
			if(!usersList.containsKey(targetId)) {
				out.write(errorMsg.getBytes("UTF-8"));
				out.flush();
				flag = true;
				continue;
			}
				
		}while(flag);
	
		//消息时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		msg = new StringBuilder().append(user.getNick()).append("(").
				append(dateFormat.format(new Date())).append(")").
				append(":").append(msg).toString();
		
		//判断目标用户是否在线
		if(usersList.get(targetId).isOnline()) {
			//得到目标用户的套接字
			socket2 = usersList.get(targetId).getSocket();	
			OutputStream out2 = socket2.getOutputStream();
			
			//给目标用户发送消息
			out2.write(msg.getBytes("UTF-8"));
			out2.flush();				
		}
		//把消息存入离线消息列表中
		else {
			usersList.get(targetId).msgList.add(msg);
		}
	}

	/**
	 * 接收ID，判断用户列表中是否存在该用户
	 * 设置用户信息
	 * @param in
	 * @param buf
	 * @throws IOException
	 */
	private void receiveID(InputStream in) throws IOException {
		int size;
		byte[] buf = new byte[256];
		size = in.read(buf);
		id = new String(buf, 0, size, "UTF-8");
		if(!usersList.containsKey(id)) {
			user = new User();
			user.setId(id);
			//接收昵称
			size = in.read(buf);
			user.setNick(new String(buf, 0, size, "UTF-8"));
			user.setSocket(socket1);
			user.setOnline(true);
			usersList.put(id, user);
		}
		else {
			user = usersList.get(id);
			//设置用户信息
			//接收昵称
			size = in.read(buf);
			user.setNick(new String(buf, 0, size, "UTF-8"));
			user.setSocket(socket1);
			user.setOnline(true);
		}
	}


	/**
	 * 发送离线信息
	 * @param out
	 * @throws IOException
	 */
	private void sendOutlineMsg(OutputStream out) throws IOException {
		ArrayList<String> msgList = usersList.get(id).getMsgList();
		String msg = new Gson().toJson(msgList);
		out.write(msg.getBytes("UTF-8"));
		out.flush();
	}


	/**
	 * 发送用户在线信息
	 * @param out
	 * @throws IOException
	 */
	private void sendUsersMsg(OutputStream out) throws IOException {
		//发送在线用户信息
		HashMap<String , String> usersMsgMap = new HashMap<>();//在线用户列表
		for(String key : usersList.keySet()) {
			if(usersList.get(key).isOnline()) {//遍历找到在线的用户
				usersMsgMap.put(key, usersList.get(key).getNick());//只发送ID和昵称
			}
		}
		String usersMsg1 = new Gson().toJson(usersMsgMap);
		usersMsg1 = new StringBuilder().append("在线用户：").
				append(usersMsg1).toString();
		
		//发送离线用户信息
		usersMsgMap = new HashMap<>();//离线用户列表
		for(String key : usersList.keySet()) {
			if(!usersList.get(key).isOnline()) {//遍历找到离线的用户
				usersMsgMap.put(key, usersList.get(key).getNick());//只发送ID和昵称
			}
		}
		String usersMsg2 = new Gson().toJson(usersMsgMap);
		usersMsg1 = new StringBuilder().append(usersMsg1).
				append("  离线用户：").append(usersMsg2).toString();
		out.write(usersMsg1.getBytes("UTF-8"));
		out.flush();
	}


	public static void main(String[] args) {
//		ArrayList<MessageInfo> msgList = new ArrayList<>();
//		User user;
//		HashMap<String, User> usersList = new HashMap<>();
//		try {
//			user = new User("1", "Jack", InetAddress.getByName("127.0.0.1"),
//					9001, true, msgList);
//			
//			usersList.put("1", user);
//			usersList.put("2", user);
//			System.out.println(usersList);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		String string = new Gson().toJson(usersList);
////		System.out.println(string);
//		
//		usersList = new Gson().fromJson(string, HashMap.class);
//		System.out.println(usersList);
		
		String msg = " 100 01：吴泊宁。";
		System.out.println(msg.indexOf(":"));
		String ID = msg.substring(msg.indexOf("：")+1);
		System.out.println(ID);
//		System.out.println(ID.length());
//		System.out.println(ID.replace(" ", ""));
				
	}


}
