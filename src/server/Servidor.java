package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import components.ChatListener;
import components.ChatWriter;

public class Servidor extends Thread {
	private static ServerSocket server;
	public BufferedReader msgReader;
	public BufferedWriter msgWriter;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private OutputStream outputStream;
	private Writer oWriter;
	private Socket socket;

	public BufferedReader getMsgReader()
	{
		return this.msgReader;
	}

	public BufferedWriter getMsgWriter()
	{
		return this.msgWriter;
	}

	public Servidor(Socket socket) 
	{
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
		server.close();
		socket.close();
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) 
	{
		try 
		{
			Semaphore semaphore = new Semaphore(1);
			ServerSocket server = new ServerSocket(12345);
			Socket socket = null;

			Scanner scan = new Scanner(System.in);
			System.out.println("SAY MY NAME: ");
			String name = scan.nextLine();
			System.out.println("Waiting connections...");

			while(socket == null) socket = server.accept();

			System.out.println("Client connected!");
			Servidor servidor = new Servidor(socket);
			Thread writer = new ChatWriter(servidor.getMsgWriter(), semaphore, name);
			Thread reader = new ChatListener(servidor.getMsgReader(), semaphore);
			
			writer.start();
			reader.start();
			System.out.println("Chat is now online.");
			
			try 
			{
				writer.join();
				servidor.exit();
				server.close();
				System.out.println("Server is now offline");
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