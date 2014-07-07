package de.tinf13b2.joc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JOCChatClientInterface extends Remote {
	public void message(String from, String to,String message, long time) throws RemoteException;
	public void message(String from, String message, long time) throws RemoteException;
	public String getUser() throws RemoteException;
	public void setID(int ID) throws RemoteException;
	public int getID() throws RemoteException;
}
