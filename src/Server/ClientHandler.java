package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import Model.Topic;

public class ClientHandler implements Runnable {

    Socket s;
    /* possiamo avere una hashmap per ogni thread, o condividerla tra tutti */
    HashMap<String, Topic> information = new HashMap<String, Topic>();

    public ClientHandler(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            Scanner from = new Scanner(s.getInputStream());
            PrintWriter to = new PrintWriter(s.getOutputStream(), true);

            System.out.println("Thread " + Thread.currentThread() + " listening...");

            boolean closed = false;
            while (!closed) {
                String request = from.nextLine();
                if (!Thread.interrupted()) {
                    System.out.println("Request: " + request);
                    String[] parts = request.split(" ");
                    switch (parts[0]) {
                        case "quit":
                            closed = true;
                            break;
                        case "info":
                            if (parts.length > 1) {
                                String key = parts[1];
                                Topic response = information.getOrDefault(key, null);
                                to.println(response);
                            } else {
                                to.println("No key");
                            }
                            break;

                        default:
                            to.println("Unknown cmd");
                    }
                } else {
                    to.println("quit");
                    //closed = true;
                    break;
                }
            }
            from.close();
            to.println("quit");
            s.close();
            System.out.println("Closed socket");
        } catch (IOException e) {
            System.err.println("ClientHandler: IOException caught: " + e);
            e.printStackTrace();
        }
    }

}
