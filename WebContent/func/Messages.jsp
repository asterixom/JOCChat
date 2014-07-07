<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="de.tinf13b2.joc.mysql.Connector"%>
<%@page import="java.util.Map.Entry"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="de.tinf13b2.joc.chat.*"%>
<%@ page import="de.tinf13b2.joc.authentication.*"%>
<%@ page import="java.util.concurrent.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map.*"%>
{ "since": "<%=System.currentTimeMillis()%>", "msgs" : [
<%
	boolean b = false;
	Chat chat = Chat.getInstance();
	String sessionID = null;
	if (request.getCookies() != null) {
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("sessionID")) {
				sessionID = cookie.getValue();
				break;
			}
		}
	}
	if (Users.validate(sessionID)) {
		Session sess = Users.getSession(sessionID);
		sess.ping();
		String sendmsg = "", recipient = "";
		if ((sendmsg = request.getParameter("sendmsg")) != null && (recipient = request.getParameter("recipient")) != null) {
			b = chat.senden(sess.getName(),recipient,sendmsg);
		} else {
			Connector db = Connector.getInstance();
			Long l = null;
			try {
				l = Long.parseLong(request.getParameter("since"));
			} catch (Exception e) {
				//l = new Long(0);
			}
			ResultSet result = db.executeQuery("select Sender,Time,Message from Messages where Reciever='"+sess.getName()+"' and Time>"+since);
			
			try {
				String minus = "";
				while (result.next()) {
					out.print(minus + "{ \"sender\": \""
							+ result.getString("Sender") + "\", \"msgs\": [");
					String plus = "";
					for (Entry<Long, String> msg : entry.getValue()
							.entrySet()) {
						out.print(plus);
						plus = ",";
						out.print("{ \"time\": \"" + msg.getKey()
								+ "\",");
						out.print("\"message\": \"" + msg.getValue()
								+ "\"}");
					}
					out.print("]}");
					minus = ",";
				}
			}catch(SQLException e){
				
			}
%>
], "groupmsg": [
<%
	HashMap<String, TreeMap<Long, String[]>> groupmsg = chat
					.getGroupMessages(sess.getName(), l);
			if (groupmsg != null) {
				String minus = "";
				for (Entry<String, TreeMap<Long, String[]>> entry : groupmsg
						.entrySet()) {
					out.print(minus + "{ \"group\": \""
							+ entry.getKey() + "\", \"msgs\": [");
					String plus = "";
					for (Entry<Long, String[]> msg : entry.getValue()
							.entrySet()) {
						out.print(plus);
						plus = ",";
						out.print("{ \"time\": \"" + msg.getKey()
								+ "\",");
						out.print("\"sender\": \"" + msg.getValue()[0]
								+ "\",");
						out.print("\"message\": \"" + msg.getValue()[1]
								+ "\"}");
					}
					out.print("]}");
					minus = ",";
				}
			}
		}
	}
%>
],"boole":<%=b%>}
