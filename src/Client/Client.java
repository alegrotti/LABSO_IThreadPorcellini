package Client;

import java.io.IOException;
import java.net.Socket;

public class Client {
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java Client <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket s = null;

        try {
            s = new Socket(host, port);
            System.out.println("Connected to server");

            System.out.println("Available commands: publish <topic>, subscribe <topic>, show, quit");

            Thread sender = new Thread(new ClientSender(s));
            Thread receiver = new Thread(new ClientReceiver(s, sender));

            sender.start();
            receiver.start();

            receiver.join();
            sender.join();
        } catch (IOException e) {
            System.err.println("Connection denied - Check address and port");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (s != null && !s.isClosed()) {
                try {
                    s.close();
                    System.out.println("Socket closed.");
                } catch (IOException e) {
                    System.err.println("Failed to close socket: " + e.getMessage());
                }
            }
        }
    }
}


