package de.tinf13b2.joc.rmi;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatWindow extends JFrame {

	public static void main(String[] args) {
		new ChatWindow();
	}
	
	private JTextField ip,name;
	private JPasswordField password;
	private JTextArea output;
	private JTextField inputText;
	private JButton send,connect;
	private ChatClient chatclient;
	
	public ChatWindow(){
		try {
			chatclient = new ChatClient(this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		setLayout(new BorderLayout());
		JPanel login = new JPanel(new FlowLayout());
		ip = new JTextField();
		ip.setText("127.0.0.1");
		ip.setPreferredSize(new Dimension(60,20));
		name = new JTextField();
		name.setPreferredSize(new Dimension(60,20));
		password = new JPasswordField();
		password.setPreferredSize(new Dimension(60,20));
		connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chatclient.connect(ip.getText(), password.getText());
			}
		});
		output = new JTextArea();
		output.setEditable(false);
		output.setAutoscrolls(true);
		output.setPreferredSize(new Dimension(300,300));
		login.add(new JLabel("IP:"));
		login.add(ip);
		login.add(new JLabel("Name:"));
		login.add(name);
		login.add(new JLabel("Password"));
		login.add(password);
		login.add(connect);
		add(login,BorderLayout.NORTH);
		add(output, BorderLayout.CENTER);
		
		JPanel input = new JPanel(new FlowLayout());
		inputText = new JTextField();
		inputText.setPreferredSize(new Dimension(250,20));
		send = new JButton("Senden");
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chatclient.post(inputText.getText());
				inputText.setText("");
			}
		});
		input.add(inputText);
		input.add(send);
		add(input,BorderLayout.SOUTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public JTextField getIp() {
		return ip;
	}

	public JTextField getUserName() {
		return name;
	}

	public JPasswordField getPassword() {
		return password;
	}

	public JTextArea getOutput() {
		return output;
	}

	public JTextField getInputText() {
		return inputText;
	}
	
	public void enableFields(boolean ok){
		ip.setEnabled(ok);
		name.setEnabled(ok);
		password.setEnabled(ok);
		connect.setEnabled(ok);
	}

}
