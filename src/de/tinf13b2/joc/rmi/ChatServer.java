package de.tinf13b2.joc.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.tinf13b2.joc.authentication.*;
import de.tinf13b2.joc.chat.Chat;
import de.tinf13b2.joc.chat.Commands;

public class ChatServer extends UnicastRemoteObject implements JOCChatServerInterface {

	private static ChatServer chatServer = null;
	private HashMap<Integer, JOCChatClientInterface> clients;
	
	private ChatServer() throws RemoteException{
		clients = new HashMap<Integer, JOCChatClientInterface>();
		Users.init();
		System.out.println("Started!");
	}

	@Override
	public void unregister(int sessionID) throws RemoteException {
		Users.removeSession(sessionID);
		clients.remove(sessionID);
	}

	@Override
	public void post(int sessionID, String to, String message) throws RemoteException {
		if(Users.validate(sessionID)){
			if(Chat.getInstance().senden(Users.getName(sessionID), to, message));
				//System.out.println("Posted!");
		}else{
			System.out.println("Not allowed!");
		}
	}

	@Override
	public boolean register(JOCChatClientInterface client, String password)
			throws RemoteException {
		String user = client.getUser();
		//System.out.println("Loggin in: "+user);
		if(Users.hasUser(user) && Users.checkPass(user, password)){
			Session sess = new Session();
			sess.setRMIClient(true);
			client.setID(sess.hashCode());
			Users.setSession(user, sess);
			clients.put(sess.hashCode(), client);
			Commands.command(user, ":all", "add", "");
			System.out.println("Logged in: "+user);
			return true;
		}
		return false;
	}

	@Override
	public String[] getOnline(int sessionID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ping(int sessionID) throws RemoteException {
		Users.getSession(sessionID).ping();
	}
	
	public void updateMessage(String to){
		try{
			JOCChatClientInterface client = clients.get(Users.getSessionByName(to).hashCode());
		//	TreeMap<Long, String[]> msgs = Chat.getInstance().getGroupMessages(to);
//			for(Entry<Long, String[]> entry : msgs.entrySet()){
//				client.message(entry.getValue()[0], entry.getValue()[1], entry.getKey());
//			}
		}catch(Exception e){
		}
	}
	
	public static ChatServer getInstance(){
		if(chatServer == null){
			try {
				chatServer = new ChatServer();
				LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
				Naming.rebind("JOCChat", chatServer);
			} catch (RemoteException | MalformedURLException e) {
				System.out.println("RMI-Server not Running properly.");
				e.printStackTrace();
			}
			
		}
		return chatServer;
	}
	
	public static void shutdown(){
		try {
			Naming.unbind("JOCChat");
			chatServer = null;
		} catch (Exception e) {
		}
	}

//	public static void main(String[] args){
//		try {
//			getInstance();
//		} catch (RemoteException | MalformedURLException e) {
//			e.printStackTrace();
//		}
//	}
}
