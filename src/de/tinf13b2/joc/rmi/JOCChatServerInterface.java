package de.tinf13b2.joc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JOCChatServerInterface extends Remote{
	public boolean register(JOCChatClientInterface client,String password) throws RemoteException;
	public void unregister(int sessionID) throws RemoteException;
	public void post(int sessionID, String to, String message) throws RemoteException;
	public String[] getOnline(int sessionID) throws RemoteException;
	public void ping(int sessionID) throws RemoteException;
}
