package Client;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientReceiver implements Runnable {

    private Socket s;
    private Thread sender;
    private Scanner from;

    public ClientReceiver(Socket s, Thread sender) {
        this.s = s;
        this.sender = sender;
    }

    @Override
    public void run() {
        try {
            from = new Scanner(this.s.getInputStream());
            while (!Thread.currentThread().isInterrupted()) {
                String response = from.nextLine();
                    if (response.equals("quit")) {
                        this.sender.interrupt();
                        break;
                    }
                    System.out.println(response);
            }
        } catch (NoSuchElementException e) {
            System.err.println("Error in the connection with server.");
        } catch (IOException e) {
            System.err.println("Error in the communication with server");
        } finally {
            if (from != null) {
                from.close();
            }
            System.out.println("Receiver closed.");
        }
    }
}
