package components;

import java.io.BufferedWriter;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class ChatWriter extends Thread
{
    private BufferedWriter writer;
    private Semaphore semaphore;
    private Scanner scanner;
    private String msg = "";
    private String name = "";

    public ChatWriter(BufferedWriter w, Semaphore s, String n)
    {
        writer = w;
        semaphore = s;
        name = n;
    }

    @Override
    public void run() 
    {
        try 
        {
            scanner = new Scanner(System.in);
            msg = "";
            while(true)
            {
            	if(scanner.hasNext())
            	{
	                semaphore.acquire();
	                msg = scanner.nextLine();
	                msg = name + " says: " + msg;
	                System.out.println(msg);
	                writer.write(msg + "\r\n");
	                writer.flush();
	                semaphore.release();
	                if(msg.equalsIgnoreCase("exit")) break;
            	}
            }
            //writer.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            semaphore.release();
        }        
	}
}