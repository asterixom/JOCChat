package de.tinf13b2.joc.authentication;

import de.tinf13b2.joc.rmi.ChatServer;


public class Session {
	private String name;
	private volatile boolean updateText;
	private boolean isRMIClient = false; //falls der Benutzer einen RMI-Client verwendet, für Webengineering nicht relevant
	private int SessionID = -1;
	
	public Session(int ID){
		SessionID = ID;
	}
	public Session(){
		SessionID = super.hashCode();
	}
	@Override
	public int hashCode(){
		return SessionID;
	}
	public String getName() {
		return name;
	}
	public boolean isOnline() {
		return Users.isOnline(name);
	}
	public void ping(){
		Users.ping(SessionID);
	}
	
	public synchronized void notifyX(){
		notifyAll();
	}
	
	public synchronized void update(){
		if(!isRMIClient){
			updateText = true;
			notifyAll();
		}else{
			//Dies ist nur für den RMI-Client und für Webengineering nicht relevant
			//System.out.println("update!");
			ChatServer.getInstance().updateMessage(name);
		}
	}
	public synchronized boolean waitForText(){
		ping();
		try{
//			int i = 0;
//			while(update == null && i<300){
//				Thread.sleep(100);
//				i++;
//			}
			long waitingSince = System.currentTimeMillis();
			while(!updateText&&System.currentTimeMillis()-waitingSince<30000){
				wait();
			}
		}catch(InterruptedException e){
			
		}
		boolean temp = updateText;
		updateText = false;
		return temp;
	}
	public boolean isRMIClient() {
		return isRMIClient;
	}
	public void setRMIClient(boolean isRMIClient) {
		this.isRMIClient = isRMIClient;
	}
}
