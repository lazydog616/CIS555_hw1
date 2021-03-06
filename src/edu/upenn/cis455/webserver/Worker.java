package edu.upenn.cis455.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Worker implements Runnable{
	private Socket socket;
	private PrintStream socket_out_printstream;
	//private ThreadPool owner_threadpool;
	private SimpleDateFormat date_ft = 
		      new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
	
	public Worker(Socket s)
	{
		socket = s;
	}
	private String GetMIME(String file_format)
	{
		String re_MIME = "";
		switch(file_format)
		{
		case "png" :  	re_MIME = "image/png";
						break;
		case "jpg":   	re_MIME = "image/jpeg";
						break;
		case "jpeg": 	re_MIME = "image/jpeg";
						break;
		case "gif":   	re_MIME = "image/gif";
						break;
		case "bmp":   	re_MIME = "image/bmp";
						break;
		case "txt":   	re_MIME = "text/plain";
						break;
		case "html":  	re_MIME = "text/html";
						break;
		default:		re_MIME = "null";
						break;
						
		}
		
		return re_MIME;
	}
	
	private void ProcessGETRequest(String[] request)
	{
		String file_format = "";
		String HTTPversion = "HTTP/1.0";//default
		if(request.length == 3)//normal GET request: GET /path/file or dire HTTP/1.1 or HTTP/1.0
		{
			String dire[] = request[1].split("\\.");
			if(dire.length < 2)//this is a directory request
			{
				
				File homedir = new File(System.getProperty("user.home"));
				File folder = new File(homedir, request[1]);
				File dires[] = folder.listFiles();
				for(int i = 0; i < dires.length; i++)
				{	
					socket_out_printstream.println(dires[i].getName());
				}
				socket_out_printstream.flush();
				socket_out_printstream.close();
				return;
			}
			else if(dire.length == 2)  //this is a file request
			{
				file_format = dire[1];//second string is format
				HTTPversion = request[2];
				//debug message
				System.out.println(file_format);
				System.out.println(request[1]);
				//end debug
				
				File homedir = new File(System.getProperty("user.home"));
				File file = new File(homedir, request[1]);
				try {
					FileInputStream file_in = new FileInputStream(file);
					byte file_content[] = new byte[(int)file.length()];
					try {
						file_in.read(file_content);
						String MIME = GetMIME(file_format);
						//start write into the outputPrintStream;
						if(MIME != "null")
						{
							socket_out_printstream.println(HTTPversion + " 200 OK");
							socket_out_printstream.println("Date: " +  date_ft.format(new Date()));
							socket_out_printstream.println("Content-Type: " + MIME);
							socket_out_printstream.println("Content-Length: " + Integer.toString((int)file.length()));
							socket_out_printstream.println("Connection: Close");
							socket_out_printstream.print("\r\n");
							socket_out_printstream.write(file_content);
						}
						else {
							socket_out_printstream.println(HTTPversion + " 500 Internal Server Error\n\nFile " + file.getAbsolutePath() + " file formant cannot be read");
							socket_out_printstream.println("Connection: Close");
						}
						socket_out_printstream.flush();
		    			socket_out_printstream.close();
						file_in.close();
						return;
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						socket_out_printstream.println(HTTPversion + " 500 Internal Server Error\n\nFile " + file.getAbsolutePath() + " cannot be read");
						socket_out_printstream.println("Connection: Close");
						socket_out_printstream.flush();
		    			socket_out_printstream.close();
		    			e.printStackTrace();
		    			return;
					}
					
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					socket_out_printstream.println(HTTPversion + " 500 Internal Server Error\n\nFile " + file.getAbsolutePath() + " cannot be found");
					socket_out_printstream.println("Connection: Close");
					socket_out_printstream.flush();
					socket_out_printstream.close();
					e.printStackTrace();
					return;
				}
				
			}
			else //file path contain "."
			{
				socket_out_printstream.println(HTTPversion + " 400 Bad Request\n\n Path contain illegal symbol");
				socket_out_printstream.println("Connection: Close");
				socket_out_printstream.flush();
				socket_out_printstream.close();
			}
		}
		else if(request.length == 2)//special GET request: GET /shutdown or GET /control
		{
			
		}
		else
		{
			socket_out_printstream.println(HTTPversion + " 400 Bad Request");
			socket_out_printstream.println("Connection: Close");
			socket_out_printstream.flush();
			socket_out_printstream.close();
			return;
		}
		
		
	}
	
	private void ProcessHEADRequest(String[] request)//similar to ProcessGETRequest
	{
		String file_format = "";
		String HTTPversion = "HTTP/1.0";//default
		if(request.length == 3)
		{
			String dire[] = request[1].split("\\.");
			if(dire.length < 2)//this is a directory request
			{
				
				File homedir = new File(System.getProperty("user.home"));
				File folder = new File(homedir, request[1]);
				File dires[] = folder.listFiles();
				for(int i = 0; i < dires.length; i++)
				{	
					socket_out_printstream.println(dires[i].getName());
				}
				socket_out_printstream.flush();
				socket_out_printstream.close();
				return;
			}
			else   //this is a file request
			{
				file_format = dire[1];//second string is format
				HTTPversion = request[2];
				//debug message
				System.out.println(file_format);
				System.out.println(request[1]);
				//end debug
				
				File homedir = new File(System.getProperty("user.home"));
				File file = new File(homedir, request[1]);
				try {
					FileInputStream file_in = new FileInputStream(file);
					byte file_content[] = new byte[(int)file.length()];
					try {
						file_in.read(file_content);
						String MIME = GetMIME(file_format);
						//start write into the outputStream;
						if(MIME != "null")
						{
							socket_out_printstream.println(HTTPversion + " 200 OK");
							socket_out_printstream.println("Date: " +  date_ft.format(new Date()));
							socket_out_printstream.println("Content-Type: " + MIME);
							socket_out_printstream.println("Content-Length: " + Integer.toString((int)file.length()));
							socket_out_printstream.println("Connection: Close");
						}
						else{
							socket_out_printstream.println(HTTPversion + " 500 Internal Server Error\n\nFile " + file.getAbsolutePath() + " file formant cannot be read");
							socket_out_printstream.println("Connection: Close");
						}
						socket_out_printstream.flush();
		    			socket_out_printstream.close();
						file_in.close();
						return;
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						socket_out_printstream.println(HTTPversion + " 500 Internal Server Error\n\nFile " + file.getAbsolutePath() + " cannot be read");
						socket_out_printstream.println("Connection: Close");
						socket_out_printstream.flush();
		    			socket_out_printstream.close();
		    			e.printStackTrace();
		    			return;
					}
					
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					socket_out_printstream.println(HTTPversion + " 500 Internal Server Error\n\nFile " + file.getAbsolutePath() + " cannot be found");
					socket_out_printstream.println("Connection: Close");
					socket_out_printstream.flush();
					socket_out_printstream.close();
					e.printStackTrace();
					return;
				}
				
			}
		}
		else
		{
			socket_out_printstream.println(HTTPversion + " 400 Bad Request");
			socket_out_printstream.println("Connection: Close");
			socket_out_printstream.flush();
			socket_out_printstream.close();
			return;
		}
		
	}
	
	public void run()
	{
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(socket.getInputStream());
			BufferedReader in = new BufferedReader(reader);
			String ori_request = in.readLine();//original request line, keep for test
			socket_out_printstream = new PrintStream(socket.getOutputStream(), true);
			//for debug : print out the request message
			System.out.println(ori_request);
	        String request[] = ori_request.split(" "); 
	        switch (request[0])
	        {
	        case "GET": this.ProcessGETRequest(request);
	        			break;
	        case "HEAD": this.ProcessHEADRequest(request);
	        			break;
	        default: socket_out_printstream.println("HTTP/1.0 400 Bad Request\n\nNot understood: \""+request+"\"");
	       
	        }
	        socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}

}
