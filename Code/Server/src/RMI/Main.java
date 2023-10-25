package RMI;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("deprecation")
public class Main {
	
	public static void main(String[] args) {
		try {
			String ip ;
			int port = 1099;

			System.out.println("Enter IP address\nDefaults to your computer's local host if nothing is entered:");
			Scanner s = new Scanner(System.in);
			ip = s.nextLine();
			if (ip == null || ip.equals("") )
				ip = InetAddress.getLocalHost().getHostAddress();

			String[] arrOfStr = ip.split("\\.");

			if(arrOfStr.length != 4){
				throw new IllegalArgumentException("IP_ADDRESS has to have 3 dots");
			}

			for (String a : arrOfStr){
				if(a.matches("^[a-zA-Z]*$")){
					throw new IllegalArgumentException("IP_ADDRESS has to only be numbers");
				}
			}

			System.out.println("My ip is : " + ip);
			System.out.println("Port :  : " + port);

			java.rmi.registry.LocateRegistry.createRegistry(port);

			ChatServer b = new ChatServer();
			Naming.rebind("rmi://" + ip + "/chat", b);
			System.out.println("[System] Chat Server is ready.");
			System.err.println("Server ready");
			
		} catch (Exception e) {
			System.out.println("Chat Server failed: " + e);
		}
	}
}