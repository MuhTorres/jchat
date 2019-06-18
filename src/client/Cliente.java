package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import components.*;

public class Cliente extends Thread{
	private BufferedReader msgReader;
	private BufferedWriter msgWriter;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private OutputStream outputStream;
	private Writer oWriter;
	
	private Socket socket;

	public Cliente(Socket socket) {
		this.socket = socket;
		try 
		{
			inputStream = socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			msgReader = new BufferedReader(inputStreamReader);

			outputStream = socket.getOutputStream();
			oWriter = new OutputStreamWriter(outputStream);
			msgWriter = new BufferedWriter(oWriter);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void exit() throws IOException 
	{
		msgReader.close();
		msgWriter.close();
		socket.close();
	}

	public BufferedReader getMsgReader()
	{
		return this.msgReader;
	}

	public BufferedWriter getMsgWriter()
	{
		return this.msgWriter;
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		try 
		{
			Scanner scan = new Scanner(System.in);
			System.out.println("SAY MY NAME: ");
			String name = scan.nextLine();
			
			Socket socket = new Socket("127.0.0.1", 12345);
			Semaphore semaphore = new Semaphore(1);
			Cliente client = new Cliente(socket);
			Thread writer = new ChatWriter(client.getMsgWriter(), semaphore, name);
			Thread reader = new ChatListener(client.getMsgReader(), semaphore);

			reader.start();
			writer.start();
			
			try 
			{
				writer.join();
				client.exit();
				socket.close();
				System.out.println("desconectado");
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		} 
		catch (Exception e) 
		{
		}
		
	}
}
