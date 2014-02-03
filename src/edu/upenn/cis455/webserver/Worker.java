package edu.upenn.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Worker implements Runnable{
	private Socket socket;
	
	public Worker(Socket s)
	{
		socket = s;
	}
	
	public void run()
	{
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(socket.getInputStream());
			BufferedReader in = new BufferedReader(reader);
			String request = in.readLine();
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

	        
	        out.println(request);
	        
	        if (request.equals("GET / HTTP/1.1"))
	          out.println("HTTP/1.1 200 OK\n\n<html><body>Hello world!</body></html>\n");
	        else
	          out.println("HTTP/1.1 500 Error\n\nNot understood: \""+request+"\"");
	        socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}

}
