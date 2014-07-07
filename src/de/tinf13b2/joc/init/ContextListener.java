package de.tinf13b2.joc.init;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.tinf13b2.joc.authentication.Users;
import de.tinf13b2.joc.mysql.Connector;
import de.tinf13b2.joc.rmi.ChatServer;

public class ContextListener implements ServletContextListener{
	private ChatServer rmiServer = null;

    public void contextInitialized(ServletContextEvent sce) {
    	System.out.println("INIT!");
        if (rmiServer == null) {
            rmiServer = ChatServer.getInstance();
        }
        Users.init();
        try {
			Connector.getInstance();
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("MYSQL: JDBC-Driver could not be loaded!");
			e.printStackTrace();
		}
    }

    public void contextDestroyed(ServletContextEvent sce){
    	System.out.println("BYE!");
        try {
        	Connector.getInstance().shutdown();
        } catch (Exception ex) {
        }
        try {
        	ChatServer.shutdown();
        } catch (Exception ex) {
        }
    }
}
