package Client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientReceiver implements Runnable {

    Socket s;
    Thread sender;
    Scanner from;

    public ClientReceiver(Socket s, Thread sender) {
        this.s = s;
        this.sender = sender;
    }

    @Override
    public void run() {
        try {
            from = new Scanner(this.s.getInputStream());
            while (true) {
                String response = from.nextLine();
                if (response.equals("quit")) {
                    this.sender.interrupt();
                    break;
                }
                System.out.println(response);
            }
            from.close();
        } catch (IOException e) {
            System.err.println("IOException caught: " + e);
        } finally {
            System.out.println("Receiver closed.");
        }
    }
}