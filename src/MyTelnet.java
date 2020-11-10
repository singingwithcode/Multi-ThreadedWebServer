/*--------------------------------------------------------

1. Name / Date:

	singingwithcode / Feb 5 5th, 2017

2. Java version used, if not the official version for the class:

	build 1.8.0_111-b14

3. Precise command-line compilation examples / instructions:

	To start:
	> javac MyTelnet.java
	> java MyTelnet

4. Precise examples / instructions to run this program:

	> javac MyTelnet.java condor.depaul.edu
	> java MyTelnet condor.depaul.edu

5. List of files needed for running the program.

  	a. MyTelnet.java

5. Notes:

	Works!

----------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MyTelnet {
	
	public static void main(String args[]) {
		String serverName;
		if (args.length < 1) {
			serverName = "localhost";
		} else {
			serverName = args[0];
		}

		System.out.println("Using server: " + serverName + ", Port: 80");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); //For input
		try {
			String name;
			do {
				System.out.print(""); 
				System.out.flush();
				name = in.readLine(); //Obtain user input, wait
				if (name.indexOf("quit") < 0) 
					getRemoteAddress(name, serverName);
			} while (name.indexOf("quit") < 0); //Bad implementation of "quit" in data
			System.out.println("Cancelled by user request."); 
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	//Helper method converting to text
	static String toText(byte ip[]) { 
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < ip.length; ++i) {
			if (i > 0)
				result.append(".");
			result.append(0xff & ip[i]);
		}
		return result.toString();
	}

	//Establishes Connection and Prints Response 
	static void getRemoteAddress(String name, String serverName) {
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;

		try {
			
			//Port has to match Server
			sock = new Socket(serverName, 80);

			//Creates filter I/O streams for the socket
			fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer = new PrintStream(sock.getOutputStream());
			
			//Sends machine name or IP address to server
			toServer.println(name);
			toServer.flush();

			//Reads in messages from the server and prints
			for (int i = 1; i <= 3; i++) {
				textFromServer = fromServer.readLine(); 
				if (textFromServer != null)
					System.out.println(textFromServer); //Our output
					log(textFromServer);
			}
			sock.close(); //Always have to close socket
		} catch (IOException x) {
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}
	
	public static void log(String message) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("clientlog.txt", true), true);
		} catch (IOException e) {
			log("Logging error");
		}
		out.write(message + System.lineSeparator());
		out.close();
	}
}