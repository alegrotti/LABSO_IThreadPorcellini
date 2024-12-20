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
    Scanner from;
    List<Message> messaggesPublisher;

    public ClientHandler(Socket s, TopicHandler rsc) {
        this.s = s;
        this.rsc = rsc;
        to = null;
        from = null;
    }

    @Override
    public void run() {
        try {
            from = new Scanner(s.getInputStream());
            to = new PrintWriter(s.getOutputStream(), true);
            
            System.out.println("Thread " + Thread.currentThread().getName() + " listening...");

            boolean closed = false;
            boolean subscriberClosed = true;
            boolean publisherClosed = true;

            while (!closed) {
                String request = from.nextLine();
                if (!Thread.interrupted()) {
                    System.out.println("Client request: " + request);
                    try{
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
                                    to.println("Client has become a subscriber\n" + "Available commands: listall, quit");
                                    break;
                                } else
                                    to.println("No key");
                                break;
                            case "publish" : 
                                if (parts.length > 1) {
                                    topic = parts[1];
                                    publisherClosed = false;
                                    closed = true;
                                    rsc.addTopic(topic);
                                    messaggesPublisher = new ArrayList<Message>();
                                    to.println("Client has become a publisher\n" + "Available commands: send <message>, list, listall, quit");
                                    break;
                                } else
                                    to.println("No key");
                                break;
                            case "show" :
                                to.println(rsc.getTopicList());
                                break;
                            default:
                                to.println("Unknown command");
                        }
                    }catch(InterruptedException e){
                        to.println("quit");
                        break;
                    }
                } else {
                    to.println("quit");
                    break;
                }
            }

            while (!subscriberClosed) {
                String request = from.nextLine();
                
                Thread.currentThread().setName("Client - Subscriber " + topic);

                if (!Thread.interrupted()) {
                    System.out.println("Client request: " + request);
                    try{
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
                    } catch (InterruptedException e){
                        System.err.println("[ClientHandler] Error in processing client request: "+e.getMessage());
                        to.println("quit");
                        break;
                    }
                } else {
                    to.println("quit");
                    break;
                }
            }

            while (!publisherClosed) {
                String request = from.nextLine();

                Thread.currentThread().setName("Client - Publisher " + topic);

                if (!Thread.interrupted()) {
                    System.out.println("Client request: " + request);
                    try{
                        String[] parts = request.split(" ");
                        switch (parts[0]) {
                            case "send":
                                String m = request.substring(5, request.length());
                                Message message = rsc.addMessage(m, topic);
                                messaggesPublisher.add(message);
                                break;  
                            case "list":
                                to.println(rsc.getPublisherMessages(topic, messaggesPublisher));
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
                    } catch (InterruptedException e){
                        System.err.println("[ClientHandler] Error in processing client request: "+e.getMessage());
                        to.println("quit");
                        break;
                    }
                } else {
                    to.println("quit");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("[ClientHandler] An error occurred during client communication: "+e.getMessage());
        } finally {
            if (from != null) {
                from.close();
            }
            if (to != null) {
                to.close();
            }
            if (s != null && !s.isClosed()) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.err.println("[ClientHandler] Error closing socket: "+e.getMessage());
                }
            }
            System.out.println("Client handler closed.");
        }
    }

    public void sendMessage(Message m){
        to.println("New message on the Topic:\n" + m.toString());
    }

}
