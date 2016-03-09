package com.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.WebSocket;

//import com.biz.SqlBiz;


public class WebSocketSocket implements WebSocket,WebSocket.OnTextMessage,WebSocket.OnBinaryMessage,WebSocket.OnFrame{
	Logger log=Logger.getLogger(WebSocketSocket.class.getName());
	Connection connection=null;
	
	static Set<WebSocketSocket> connectionsSet=new CopyOnWriteArraySet<WebSocketSocket>();
	static Map<String,WebSocketSocket> socketList=new HashMap<String,WebSocketSocket>();
	
	private User user;
	
	public static  String bytesTarget="";
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * �յ���Ϣ
	 */
	@Override
	public void onMessage(String arg0) {
//		try{
//		byte[] b=getImg("d:/bg.jpg");
//		this.connection.sendMessage(b, 0, b.length);}catch(Exception e){}
		// TODO Auto-generated method stub
		HashMap<String,String> result = strToHash(arg0);
		try{
			
		if ("login".equals(result.get("type")) == false && this.user == null) { 	
				this.sendToSelf("result=error&error=���ȵ�¼");
    		return;
    	}
		switch(result.get("type")){
		case "login":
			if(this.user != null){
				this.sendToSelf("result=error&error=�������ظ���¼");
				break;
			}
			Script.login(result,this);
			break;
		case "talk":
			Script.talk(result,this);
			break;  	
		case "sendBytes":
			synchronized(bytesTarget){
			bytesTarget=Script.getTarget(result, this);
			}
			break;
		case "getHistoryMsg":
			//log.info("==========getHistoryMsg==========");
//			SqlBiz sb=new SqlBiz();
			try {
//				String history=sb.getHistoryMessageS(result.get("receiver"));
				String history="没有连接db,这是模拟的历史消息。";
				//log.info(history);
				sendToSelf("result=history&msg="+history);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
	} 
	}catch(IOException e){
		e.printStackTrace();	
	}
	}
	/**
	 * �ر�����
	 */
	@Override
	public void onClose(int arg0, String arg1) {
		// TODO Auto-generated method stub
		synchronized(socketList){
			socketList.remove(this.getUser().getName());			
		}
		synchronized(connectionsSet){
			connectionsSet.remove(this);
		}
		//offline
		try {
			this.sendToAll(this.getUser().getName()+"����");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * ��ʼ����
	 */
	@Override
	public void onOpen(Connection arg0) {
		// TODO Auto-generated method stub
		this.connection=arg0;
		this.connection.setMaxBinaryMessageSize(163840000);
		synchronized(connectionsSet){
			connectionsSet.add(this);
		}
		
	}
	/**
	 * �����Լ���Ϣ
	 */
	public void sendToSelf(String value) throws IOException{
		this.connection.sendMessage(value);
	}
	/**
	 * ������������Ϣ
	 */
	public void sendToAll(String value) throws IOException{
		for(WebSocketSocket socket:connectionsSet){			
			socket.connection.sendMessage(value);
		}	
	}
	/**
	 * ����ָ������Ϣ
	 */
	public void sendToName(String name,String value)throws IOException{
		boolean flag=true;
		for (WebSocketSocket socket : connectionsSet) {
			if(socket.getUser().getName().equals(name)){
				socket.connection.sendMessage(value);
				flag=false;
				break;
			}	
	}
		//if can not find the user send back
		if(flag){
		sendToSelf("���û������ߣ���ϢΪ������ʽ����");
		//save the message into lib,wait for offline user check
		//...
		
		}
	}
	/**
	 * �õ��û��б�
	 */
	public String getUserList(){
		String result="";
		String add="";
		for(WebSocketSocket socket:connectionsSet){
			result+=add+socket.getUser().getName();
			add=",";			
		}
		return result;
	}
	/**
	*��Ϣ�ַ�ת��Ϊ��ϣ��
	*/
	private HashMap<String,String> strToHash(String value){
		HashMap<String,String> result = new HashMap<String,String>();
		String[] arrValue = value.split("&"); 
		for(int i=0;i<arrValue.length;i++){
			String[] keyvalue = arrValue[i].split("=");
			if(keyvalue.length == 1){
				result.put(keyvalue[0], "");
			}else{
				result.put(keyvalue[0], keyvalue[1]);
			}
		}
		return result;
	}
	/**
	 * ��byte[]��ָ����
	 */
	public void sendBytes(String name,byte[] bytes,int arg1,int arg2)throws Exception{
		boolean flag=true;
		if(name.equals("all")){
			for (WebSocketSocket socket : connectionsSet) {
				
				socket.connection.sendMessage(bytes, arg1,arg2);
				
			}
		}
		
		for (WebSocketSocket socket : connectionsSet) {
			 if(socket.getUser().getName().equals(name)){
				socket.connection.sendMessage(bytes, arg1,arg2);
				log.info( "����byte[]");
				flag=false;
				break;
			}		
	}
		//if can not find the user send back
		if(flag){
				sendToSelf("���û������ߣ���ϢΪ������ʽ����");
				//save the message into lib,wait for offline user check
				//...
		}
		}
	/**
	 * �յ�byte��Ϣ
	 * type=xxx&target=xxx&byte=xxx
	 */
	@Override
	public void onMessage(byte[] arg0, int arg1, int arg2) {
		log.info("���յ�2������Ϣ,����Ŀ��Ϊ:"+bytesTarget);
		// TODO Auto-generated method stub	
		try {
			sendBytes(bytesTarget,arg0,arg1,arg2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onFrame(byte arg0, byte arg1, byte[] arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub

		return false;
	}

	@Override
	public void onHandshake(FrameConnection arg0) {
		// TODO Auto-generated method stub
			
	}
//	/**
//	 * ��ͼƬ���ڱ���
//	 * @param b
//	 * @throws Exception
//	 */
//	public void transImg(byte[] b)throws Exception{
//		
//		String URL="d:/test.jpg";
//
//		File file=new File(URL);
//
//		FileOutputStream fos=new FileOutputStream(file);
//
//		fos.write(b,0,b.length);
//
//		fos.flush();
//
//		fos.close(); 
//		
//		
//	}
//	/**
//	 * ��ȡͼƬ��ת��Ϊbyte[]
//	 */
//	public byte[] getImg(String filePath)throws Exception{
//		File file = new File(filePath);
//		   FileInputStream in = null;
//		   ByteArrayOutputStream out = new ByteArrayOutputStream();
//		   try {
//		   in = new FileInputStream(file);
//		   byte[] buffer = new byte[in.available()];
//		   in.read(buffer);
//		   return buffer;
//		   } catch (Exception e) {
//		   e.printStackTrace();
//		   } finally {
//		   try {
//		   if (in != null)
//		   in.close();
//		   } catch (IOException e) {
//		   // TODO Auto-generated catch block
//		   e.printStackTrace();
//		   }
//		   }
//		   return null;
//	}

}