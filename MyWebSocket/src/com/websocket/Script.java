package com.websocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

//import com.biz.SqlBiz;

public class Script {
	public Script(){
		
		
		
	}
	/**
	 * 登录
	 * type=login&name=xxx
	 */
	public static void login(HashMap<String,String> hashScript,WebSocketSocket socket)throws IOException{
		
		String name = hashScript.get("name");
		if(name == null || name.length() == 0){
			socket.sendToSelf("result=error&error=姓名不能为空");
			return;
		}
		while(WebSocketSocket.socketList.get(name) != null){
			socket.sendToSelf("您已经登录，无需再登录");
			return;
		}
		
		WebSocketSocket.socketList.put(name,socket);
		User user = new User();
		user.setName(name);
		socket.setUser(user);
		String ulist = socket.getUserList();
		socket.sendToSelf("result=loginok&name="+name);
		socket.sendToAll("result=setuserlist&list="+ulist);
		socket.sendToAll("result=talk&msg=用户【"+name+"】进入");
		
	}
	/**
	 * 发言
	 * type=talk&target=xxx&msg=xxx
	 */
	public static void talk(HashMap<String,String> hashScript,WebSocketSocket socket)throws IOException{
		String target = hashScript.get("target");
		String msg = hashScript.get("msg");
		if(target == null)return;
		if(target.equals("all")){
			socket.sendToAll("result=talk&msg=用户【"+socket.getUser().getName()+"】:"+msg);
		}else{
			socket.sendToName(target, "result=talk&msg=用户【"+socket.getUser().getName()+"】密语:"+msg);
			
//			SqlBiz sb=new SqlBiz();
			String sender=socket.getUser().getName();
			String receiver=target;
			String message=msg;
			Date date=new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String fd=format.format(date);
			String sql="('"+sender+"','"+receiver+"','"+message+"','"+fd+"')";
			try {
//				sb.insertMsg(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 得到bytes接收人
	 * type=sendBytes&target=xxx
	 */
	public static String getTarget(HashMap<String,String> hashScript,WebSocketSocket socket)throws IOException{
		String target = hashScript.get("target");		
		return target;
	}
}
