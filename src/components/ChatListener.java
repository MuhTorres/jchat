package components;

import java.io.BufferedReader;
import java.util.concurrent.Semaphore;

public class ChatListener extends Thread
{
    private BufferedReader reader;
    private Semaphore semaphore;
    private String msg = "";

    public ChatListener(BufferedReader r, Semaphore s)
    {
        reader = r;
        semaphore = s;
    }

    @Override
    public void run() 
    {
        try 
        {
            while(!msg.equalsIgnoreCase("exit") || msg != null)
            {
                if(reader.ready())
                {                	
                    semaphore.acquire();
                    msg = reader.readLine();
                    System.out.println(msg);
                    semaphore.release();
                }
            }
            reader.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            semaphore.release();
        }
	}
}