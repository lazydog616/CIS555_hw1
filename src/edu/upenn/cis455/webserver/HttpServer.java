package edu.upenn.cis455.webserver;
import java.io.*;
import java.net.*;


class HttpServer {
  private static int serverport = 8080;
  public static void main(String args[]) throws IOException, InterruptedException
  {
	  ThreadPool threadpool = new ThreadPool(3000);
	  ServerSocket serversocket = new ServerSocket(serverport, 2000);
     new Thread(threadpool).start();
     while(true)
     {
    	 Socket socket = serversocket.accept();
    	 if(socket.isConnected())
    		 threadpool.Handle(socket);
     }
     
  }
  
}
