/*--------------------------------------------------------

1. Name / Date:

	singingwithcode / Feb 5 5th, 2017

2. Java version used, if not the official version for the class:

	build 1.8.0_111-b14

3. Precise command-line compilation examples / instructions:

	To start:
	> javac MyWebServer.java
	> java MyWebServer

4. Precise examples / instructions to run this program:

	To start:
	> javac MyWebServer.java
	> java MyWebServer

5. List of files needed for running the program.

  	a. MyWebServer.java
  	b. Mozilla

5. Notes:

	 Can navigate through directories but there is a bug after 
	 you enter into a file inside a directory. I marked it as no 
	 since it is not 100%. 

----------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;

class Worker extends Thread {
	Socket sock;

	Worker(Socket s) {
		sock = s;
	}

	public void run() {
		PrintStream out = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintStream(sock.getOutputStream());
			try {

				// For printing
				String input;

				// For the first line of printing
				input = in.readLine();

				findRequestType(input, out);
				//sendToClient(input, out);
			} catch (IOException x) {
				System.out.println("Server read error");
				x.printStackTrace();
			}
			sock.close();
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

	// Serve the request
	void findRequestType(String r, PrintStream out) throws IOException {
		// Some given code / implementation was used from the 150 line server 
		if (!r.startsWith("GET") || r.length() < 14 || !(r.endsWith("HTTP/1.1") || r.endsWith("HTTP/1.0"))) {
			// Something went wrong with the request
			sendToClient("400, Bad Request", out);
		} else {
			String req = r.substring(4, r.length() - 9).trim();
			// For security reasons
			if (req.indexOf("..") != -1 || req.indexOf("/.ht") != -1 || req.endsWith("~")) {
				sendToClient("403, Forbidden", out);
			} else {
				// Clean request made
				String parentDir = new File(".").getCanonicalPath(); //Get the current path
				String filee = req.substring(req.lastIndexOf("/") + 1); //Get the name of the file
				
				//See if a further directory
				String temp = req.substring(req.indexOf("/")+1);
				String middle = temp.substring(temp.indexOf("/")+1, temp.lastIndexOf("/") + 1);
				System.out.println("This is the middle1: " + middle);
				
				String path;
				if (middle.equals("")) {
					path = parentDir + "/" + filee; //Concatenate the path
				} else {
					path = parentDir + "/" + middle + filee; //Concatenate the path
				}
				
				File f = new File(path);
				
				if (filee.isEmpty()) {
					//return directory
					
					// Setup the date
					long date = System.currentTimeMillis();
                
					// Send the Header
					sendToClient("HTTP/1.0 200 OK\r\n" + "Content-Type: " 
								+ "text/html" + "\r\n"
								+ "Date: " + date + "\r\n"
								+ "Server: FileServer 1.0\r\n\r\n", out);
					
					//Our return string with HTML
					String filedir;
					
				    File f1 = new File ("./" + middle); //File Object
				    File[] strFilesDirs = f1.listFiles(); //Get all files under directory
				    
				    filedir = "<!DOCTYPE html><html><body>"; //Header to declare HTML
				    for ( int i = 0 ; i < strFilesDirs.length ; i ++ ) {
				    	if ( strFilesDirs[i].isDirectory ( ) ) //is directory
				    		filedir = filedir + "<a href=" + '"' + strFilesDirs[i] + "/" + '"' + ">" + strFilesDirs[i] + "</a> <br>";
				    	else if ( strFilesDirs[i].isFile ( ) ) //is file
				    		filedir = filedir + "<a href=" + '"' + strFilesDirs[i] + '"' + ">" + strFilesDirs[i] + "</a> <br>";
				    }
				    filedir = filedir + "</body></html>";
				    
				    sendToClient(filedir, out);
				    //System.out.println(filedir);
				    
				} else {
					//return file
					try {
                	
						// Setup the date
						long date = System.currentTimeMillis();
                    
						// Send the Header
						sendToClient("HTTP/1.0 200 OK\r\n" + "Content-Type: " 
									+ findType(path) + "\r\n"
									+ "Date: " + date + "\r\n"
									+ "Server: FileServer 1.0\r\n\r\n", out);
									// "\r\n\r\n" declares end of line
                    
						// Setup to transfer file
						InputStream file = new FileInputStream(f);
                    
						// Send File
						// Some given code / implementation was used from the 150 line server 
						try {
							byte[] buffer = new byte[1000];
							while (file.available()>0) 
								out.write(buffer, 0, file.read(buffer));
						} catch (IOException e) { System.err.println(e); }
					} catch (FileNotFoundException e) { 
						// File not found
						sendToClient("404, File Not Found", out);
					}
				}
			}
		out.flush();
		}
	}


	
	//Looks to see what type the file is
	private static String findType(String fileName)
    {
        if (fileName.endsWith(".html")) {
            return "text/html";
        }
        else if (fileName.endsWith(".txt") || fileName.endsWith(".java")) {
            return "text/plain";
        } else {
            return "text/plain";
        }
    }

	// Print to client side
	static void sendToClient(String message, PrintStream out) {
		out.println(message);
	}
}

public class MyWebServer {

	public static void main(String a[]) throws IOException {
		int q_len = 6; // Number of requests to get at that instance in time
		int port = 2540; // Port to which we connect to
		Socket sock;

		ServerSocket servsock = new ServerSocket(port, q_len);

		System.out.println("Server listening at port " + port + ".\n");
		while (true) {
			sock = servsock.accept(); // Waiting for client connection
			new Worker(sock).start(); // Sends worker to handle it
		}
	}

	// Helper to print to ServerLog.txt
	public static void log(String message) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("serverlog.txt", true), true);
		} catch (IOException e) {
			log("Logging error");
		}
		out.write(message + System.lineSeparator());
		out.close();
	}
}