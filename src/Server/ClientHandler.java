package Server;

import java.io.*;
import java.util.*;
import java.net.Socket;

import Model.Message;

public class ClientHandler implements Runnable {

    Socket s;
    TopicHandler rsc;
    String topic;
    PrintWriter to;
    List<Message> messaggesPublisher;

    public ClientHandler(Socket s, TopicHandler rsc) {
        this.s = s;
        this.rsc = rsc;
    }

    @Override
    public void run() {
        try {
            Scanner from = new Scanner(s.getInputStream());
            to = new PrintWriter(s.getOutputStream(), true);
            
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
                                rsc.addSubscriber(topic,this);
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
                                messaggesPublisher = new ArrayList<Message>();
                                break;
                            } else {
                                to.println("No key");
                            }
                            break;
                        case "show" :
                            to.println(rsc.getTopicList());
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
                            String m = request.substring(5, request.length());
                            Message message = rsc.addMessage(m, topic);
                            messaggesPublisher.add(message);
                            break;  
                        case "list":
                            String stamp = "Messages of the publisher:";
                            for (Message mess : messaggesPublisher)
                                stamp+= "\n" + mess.toString();
                            to.println(stamp);
                            break;  
                        case "quit":
                            to.println("quit");
                            publisherClosed = true;
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

    public void sendMessage(Message m){
        to.println("New message on the Topic:\n"+m.toString());
    }

}
