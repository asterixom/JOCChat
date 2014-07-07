package de.tinf13b2.joc.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

public class ChatClient extends UnicastRemoteObject implements JOCChatClientInterface {

	private JOCChatServerInterface server;
	private int ID;
	private ChatWindow window;
	
	public ChatClient(ChatWindow w) throws RemoteException{
		window = w;
	}
	
	public void connect(String ip, String password){
		while(ip==null || !ip.matches("^[0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}$")){
			ip = JOptionPane.showInputDialog("Please enter Server-IP", "127.0.0.1");
		}
		String url = "//"+ip+"/JOCChat";
		try {
			server = (JOCChatServerInterface)Naming.lookup(url);
			window.enableFields(false);
			if(!server.register(this, password)){
				window.enableFields(true);
			}
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(true){
					try {
						Thread.sleep(10000);
						server.ping(ID);
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					}
				}
			}).start();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void message(String from, String to, String message, long time)
			throws RemoteException {
		window.getOutput().append(from+":	"+message+"	("+time+")\n");
	}

	@Override
	public void message(String from, String message, long time)
			throws RemoteException {
		window.getOutput().append(from+":	"+message+"	("+time+")\n");
	}

	@Override
	public String getUser() throws RemoteException {
		return window.getUserName().getText();
	}
	
	public void post(String text){
		try {
			server.post(ID, ":all", text);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setID(int ID) throws RemoteException {
		this.ID = ID;
	}

	@Override
	public int getID() throws RemoteException {
		return ID;
	}

	public JOCChatServerInterface getServer() {
		return server;
	}

}
