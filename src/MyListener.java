/*--------------------------------------------------------

1. Name / Date:

	singingwithcode / Feb 5 5th, 2017

2. Java version used, if not the official version for the class:

	build 1.8.0_111-b14

3. Precise command-line compilation examples / instructions:

	To start:
	> javac MyListener.java
	> java MyListener

4. Precise examples / instructions to run this program:

	> javac MyListener.java
	> java MyListener

5. List of files needed for running the program.

  	a. MyTelnet.java

5. Notes:

	Default port is 2540

----------------------------------------------------------*/
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class Workerr extends Thread {
	Socket sock;

	Workerr(Socket s) {
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
				while ((input = in.readLine()) != null) {
					System.out.println(input);
					MyListener.log(input); // Log the clientInput
				}

				sendToClient(input, out);
			} catch (IOException x) {
				System.out.println("Server read error");
				x.printStackTrace();
			}
			sock.close();
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

	// Print to client side
	static void sendToClient(String clientInput, PrintStream out) {
		out.println("Message Received");
	}
}

public class MyListener {

	public static void main(String a[]) throws IOException {
		int q_len = 6; // Number of requests to get at that instance in time
		int port = 2540; // Port to which we connect to
		Socket sock;

		ServerSocket servsock = new ServerSocket(port, q_len);

		System.out.println("Server listening at port " + port + ".\n");
		while (true) {
			sock = servsock.accept(); // Waiting for client connection
			new Workerr(sock).start(); // Sends worker to handle it
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
