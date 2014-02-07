package edu.upenn.cis455.webserver;

import java.net.Socket;
import java.util.List;

public class ThreadPool implements Runnable{
	private int poolsize = 0;
	private BlockingQueue pool;
	private List<Worker> workers;
	public ThreadPool(int size)
	{
		pool = new BlockingQueue(size);
		poolsize = size;
	}
	
	public void Handle(Socket s) throws InterruptedException
	{
		pool.enqueue(s);
	}
	public int GetWorkerAmount()
	{
		return this.workers.size();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			Socket s;
			try {
				s = (Socket) pool.dequeue();
				if(s.isConnected())
				{
					Worker worker = new Worker(s);
					workers.add(worker);
					new Thread(worker).start();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
