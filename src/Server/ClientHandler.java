package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    Socket s;
    Resource rsc;
    String topic;
    Thread publisherHandler;

    public ClientHandler(Socket s, Resource rsc) {
        this.s = s;
        this.rsc = rsc;
    }

    @Override
    public void run() {
        try {
            Scanner from = new Scanner(s.getInputStream());
            PrintWriter to = new PrintWriter(s.getOutputStream(), true);
            
            System.out.println("Thread " + Thread.currentThread() + " listening...");
            
            boolean closed = false;
            boolean subscriberClosed = true;
            boolean publisherClosed = true;

            while (!closed) {
                String request = from.nextLine();
                if (!Thread.interrupted()) {
                    System.out.println("Client request: " + request);
                    String[] parts = request.split(" ");
                    switch (parts[0]) {
                        case "quit":
                            to.println("quit");
                            closed = true;
                            break;
                        case "subscribe":
                            if (parts.length > 1) {
                                topic = parts[1];
                                subscriberClosed = false;
                                closed = true;
                                break;
                            } else {
                                to.println("No key");
                            }
                            break;
                        case "publish" : 
                            if (parts.length > 1) {
                                topic = parts[1];
                                publisherClosed = false;
                                closed = true;
                                break;
                            } else {
                                to.println("No key");
                            }
                            break;
                        case "show" :
                            to.println("Topics:\n"+rsc.getTopicList());
                            break;
                        default:
                            to.println("Unknown command");
                    }
                }
            }

            while (!subscriberClosed) {
                String request = from.nextLine();
                if (!Thread.interrupted()) {
                    System.out.println("Client request: " + request);
                    String[] parts = request.split(" ");
                    switch (parts[0]) {
                        case "quit":
                            to.println("quit");
                            subscriberClosed = true;
                            break;
                        case "listall" :
                            to.println(rsc.getMessagesList(topic));
                            break;
                        default:
                            to.println("Unknown command");
                    }
                }
            }

            while (!publisherClosed) {
                String request = from.nextLine();
                if (!Thread.interrupted()) {
                    System.out.println("Client request: " + request);
                    String[] parts = request.split(" ");
                    switch (parts[0]) {
                        case "send":
                        
                            break;  
                        case "list":
                            break;  
                        case "quit":
                            to.println("quit");
                            subscriberClosed = true;
                            break;
                        case "listall" :
                            to.println(rsc.getMessagesList(topic));
                            break;
                        default:
                            to.println("Unknown command");
                    }
                }
            }


            from.close();
            to.close();
            
            s.close();
            System.out.println("Client Closed");
        } catch (IOException e) {
            System.err.println("ClientHandler: IOException caught: " + e);
            e.printStackTrace();
        }
    }

}
