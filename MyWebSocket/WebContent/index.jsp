<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = "ws://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>聊天室</title>
		<style>
			h1 {
				font-size: 18pt;
				background: #AAAAEE;
				padding: 5px;
			}
			table tr th {
				background: #DDDDEE;
				margin: 3px;
				padding: 3px;
			}
			table tr td {
				background: #EEEEEE;
				margin: 3px;
				padding: 3px;
			}
			ul li {
				color: #333366;
			}
		</style>
		<script src="jquery.js" type="text/javascript"></script>
		<script type="text/javascript">
		var basePath = "<%=basePath%>";
			var WebSocket = window.WebSocket || window.MozWebSocket;
			var socket;
			var msg;

			function initial() {
				msg = document.getElementById("msg");
				document.getElementById("closebtn").disabled = true;
			}

			function doOpen() {
				if(document.querySelector('#name').value == "") {
					alert('请输入用户姓名。');
					return;
				}
				socket = new WebSocket(basePath+"WebSocketService");
				socket.addEventListener('open', function(e) {
					var str1 = document.getElementById("name").value;
					socket.send("type=login&name=" + str1);
				});
				socket.addEventListener('message', function(e) {
					if(typeof(e.data)!="string"){
					
					var reader=new FileReader();
						reader.readAsDataURL(e.data);
					reader.onload=function(e){
						 document.getElementById("portrait").src = e.target.result; 
							
					}
					}
					var value = getScript(e.data);
					var text;
					switch(value["result"]) {
						case "error":
							text = "<li>" + value["error"] + "</li>" + msg.innerHTML;
							break;
						case "loginok":
							document.getElementById("name").value = value["name"];
							text = "<li>" + "登录成功" + "</li>" + msg.innerHTML;
							break;
						case "talk":
							text = "<li>" + value["msg"] + "</li>" + msg.innerHTML;
							break;
						case "removeuser":
							removeUser(value["name"]);
							break;
						case "setuserlist":
							removeAllUser();
							var list = value["list"].split(",");
							for(var i=0,l=list.length;i<l;i++){
								addUser(list[i]);
							}
							break;
						case "history":
							$("#history").html(value["msg"]);
							
							break;
					}
					
					if(text)msg.innerHTML = text;
				});
				socket.addEventListener('error', function(e) {
					alert("error!!");
				});
				socket.addEventListener('close', function(e) {
					msg.innerHTML = "<li>服务器切断</li>" + msg.innerHTML;
					document.getElementById("name").disabled = false;
					document.getElementById("openbtn").disabled = false;
					document.getElementById("closebtn").disabled = true;
				});
				msg.innerHTML = "<li>连接服务器成功</li>" + msg.innerHTML;
				document.getElementById("name").disabled = true;
				document.getElementById("openbtn").disabled = true;
				document.getElementById("closebtn").disabled = false;
			}

			function getScript(value) {
				var valueArray = value.split("&");
				var arr;
				var scriptObj = {};
				for(var i in valueArray) {
					arr = valueArray[i].split("=");
					scriptObj[arr[0]] = arr[1];
				}
				return scriptObj;
			}

			function doAction() {
				if(socket.readyState == WebSocket.OPEN) {
					var to = document.getElementById("to").value;
					var message = document.getElementById("message").value;
					socket.send("type=talk&target="+to+"&msg="+message);
					document.getElementById("message").value = "";
				} else {
					alert('连接服务器失败。');
				}
				return false;
			}

			function doClose() {
				if(socket.readyState == WebSocket.OPEN) {
					socket.close();
				}
			}

			function addUser(username) {
				var list = document.getElementById("to");
				if(!isExitUser(list, username)) {
					var item = new Option(username, username);
					list.options.add(item);
				}
			}

			function removeUser(username) {
				var list = document.getElementById("to");
				for(var i = 0; i < list.options.length; i++) {
					if(list.options[i].value == username) {
						list.options.remove(i);
						break;
					}
				}
			}

			function removeAllUser() {
				var list = document.getElementById("to");
				for(var i = 1,l=list.options.length; i < l; i++) {
					list.options.remove(1);
				}
			}
			function isExitUser(username) {
				var list = document.getElementById("to");
				var isExit = false;
				for(var i = 0; i < list.options.length; i++) {
					if(list.options[i].value == username) {
						isExit = true;
						break;
					}
				}
				return isExit;
			}
			function sendImg(){
				var reader=new FileReader();
				 var file = document.getElementById("file").files[0];
				reader.readAsArrayBuffer(file);
				reader.onload=function(){
					console.log("sending"+file.name);
					socket.send("type=sendBytes&target="+$("#to").val());
					socket.send(reader.result);
					
				}
				
			}
			function fetchHistory(){
	
				socket.send("type=getHistoryMsg&receiver="+$("#name").val())
				
				
			}
		</script>
	</head>
	<body onload="initial();">
		<header>
			<h1>Web Socket 聊天室</h1>
		</header>
		<article>
			<input type="button" id="openbtn" onclick="doOpen();" value="连接服务器" />
			<input type="button" id="closebtn" onclick="doClose();" value="断开连接" />
			<hr>
			<form onsubmit="return doAction();">
				<table>
					<tr>
						<th>用户:</th>
						<td>
						<input type="text" id="name" size="10">
						</td>
					</tr>
					<tr>
						<th>消息:</th>
						<td>
						<select id="to">
							<option value="all">所有人</option>
						</select>
						<br />
						<input type="text" id="message" size="40">
						<input type="submit" value="发送">
						</td>
					</tr>
				</table>
			</form>
			<input type="file" id="file" name="file"/>
			<input type="button" value="发送图片" onclick="sendImg()"/>
			<input type="button" value="得到历史消息" onclick="fetchHistory()">
			<hr>
			<ul id="msg"></ul>
			<hr><br>
			图片区:
			 <img id="portrait" src="" > 
			 <hr><br>
			 历史区:
			 <div id="history"></div>
		</article>
	</body>
</html>