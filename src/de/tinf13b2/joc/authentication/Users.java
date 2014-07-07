package de.tinf13b2.joc.authentication;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.tinf13b2.joc.mysql.Connector;

public class Users {
	
	private static final int TimeoutMillis = 60000;

	private static MessageDigest md;
	// private boolean inited = false;
	private static Thread running;
	private static HashMap<Integer, Session> sessions;
	//private volatile static boolean updateOnline = false;
	private static Connector db;

	public Users() {
		init();
	}

	public static void init() {
		// if(inited){
		// return this;
		// }else{
		// inited = true;
		// }
		try {
			db = Connector.getInstance();
		} catch (ClassNotFoundException | SQLException e2) {
			e2.printStackTrace();
		}
		if(sessions == null){
			sessions = new HashMap<>();
//			db.executeUpdate("update Users set SessionID=NULL");
			ResultSet result = db.executeQuery("select SessionID from Users where SessionID is not null");
			try {
				while(result.next()){
					int id = result.getInt(1);
					sessions.put(id,new Session(id)); 
				}
			} catch (SQLException e) {
				e.printStackTrace(System.out);
			}
		}
		if (md == null) {
			try {
				md = MessageDigest.getInstance("SHA1");
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
		}
		if (running == null) {
			running = new Thread(new Runnable() {

				@Override
				public void run() {
					//TODO
//					while (true) {
//						try {
//							Thread.sleep(10000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						for (Iterator<Entry<Integer, Session>> i = sessions
//								.entrySet().iterator(); i.hasNext();) {
//							Entry<Integer, Session> e = i.next();
//							if (!e.getValue().checkOnline()) {
//								hashs.remove(e.getValue().getName());
//								i.remove();
//							}
//							e.getValue().notifyX();
//						}
//					}
				}
			});
			//running.start();
		}
	}
	
	public static String encrypt(String text){
		return new BigInteger(1,md.digest(text.getBytes())).toString(32);
	}

	public static boolean setPass(String name, String pass) {
		return (db.executeUpdate("update Users set Password='"+encrypt(pass)+"', LastPing = now() where Name like '"+name+"'"));
	}
	
	public static boolean createUser(String name, String pass){
		return (db.executeUpdate("insert into Users set Name='"+name+"', Password='"+encrypt(pass)+"', Type=0"));
	}
	
	public static boolean checkPass(String name, String pass) {
		ResultSet result = db.executeQuery("select Password from Users where Name like '"+name+"'");
		try {
			if(result.next() && result.getString(1).equals(encrypt(pass))){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return false;
	}

	public static boolean removeUser(String name) {
		return db.executeUpdate("delete from Users where Name like '"+name+"'");
	}

	public static boolean hasUser(String name) {
		///TO DO
		ResultSet result = db.executeQuery("select ID from Users where Name like '"+name+"'");
		try {
			if(result.next()){
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
/*
	public static void end() {
		//X XX
	}
*/
	
	public static boolean setSession(String name, Session sess) {
		if(db.executeUpdate("update Users set SessionID="+sess.hashCode()+", LastPing = now() where Name like '"+name+"'")){
			sessions.put(sess.hashCode(), sess);
			return true;
		}
		return false;
	}

	public static void removeSession(String name) {
		db.executeUpdate("update Users set SessionID=null where Name like '"+name+"'");
	}
	
	public static void removeSession(Integer hash) {
		db.executeUpdate("update Users set SessionID=null where SessionID="+hash);
	}

	public static Session getSession(Integer hash){
		return sessions.get(hash);
	}
	
	public static Session getSessionByName(String name){
		//XXX
		ResultSet result = db.executeQuery("select SessionID from Users where Name like '"+name+"'");
		try {
			if(result.next()){
				return sessions.get(result.getInt(1));
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
			return null;
		}
	}
	
	public static Session getSession(String hash){
		try{
			return getSession(Integer.parseInt(hash));
		}catch(NumberFormatException e){
			return null;
		}
	}

	public static boolean isOnline(String name) {
		ResultSet result = db.executeQuery("select ID from Users where Name like '"+name+"' and LastPing>"+(System.currentTimeMillis()-TimeoutMillis)+" and SessionID is not null");
		try {
			return result.next();
		} catch (SQLException e) {
			return false;
		}
//		ResultSet result = db.executeQuery("select LastPing from Users where Name like '"+name+"'");
//		try {
//			if(result.next() && result.getDate(1).after(new Date(System.currentTimeMillis()-TimeoutMillis))){
//				return true;
//			}else{
//				return false;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace(System.out);
//			return false;
//		}
	}

	public static Set<String> getOnline() {
		HashSet<String> set = new HashSet<>();
		ResultSet result = db.executeQuery("select Name from Users where LastPing>"+(System.currentTimeMillis()-TimeoutMillis)+" and SessionID is not null");
		try {
			while(result.next()){
				set.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
		return set;
	}
	
	public static boolean validate(String sessionID){
		//TODO
		if(sessionID==null){
			return false;
		}
		ResultSet result = db.executeQuery("select ID from Users where SessionID="+sessionID);
		try {
			if(result.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
		return false;
	}
	public static boolean validate(int sessionID){
		return validate(sessionID+"");
	}

	public static String getName(String sessionID){
		ResultSet result = db.executeQuery("select Name from Users where SessionID="+sessionID);
		try {
			if(result.next()){
				return result.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
		return null;
	}
	
	public static String getName(int sessionID){
		return getName(sessionID+"");
	}
	
	public static void ping(int sessionID){
		db.executeUpdate("update Users set LastPing = now() where SessionID="+sessionID);
	}
}
