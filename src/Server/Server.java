package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        Scanner userInput = new Scanner(System.in);

        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server is waiting for connections on port " + port 
            		+ "\nAvailable commands: quit, show, inspect <topic>");

            TopicHandler rsc = new TopicHandler();

            Thread listenerThread = new Thread ( new SocketListener(server, rsc) );
            listenerThread.start();

            boolean closed = false;
            boolean inspect = false;
            String topic = "";

            while (!closed) {
                if(!inspect){
                    String request = userInput.nextLine();
                    System.out.println("Server request: " + request);
                    String[] parts = request.split(" ");
                    switch (parts[0]) {
                        case "quit":
                            closed = true;
                            rsc.closeServer();
                            break;
                        case "show":
                            System.out.println(rsc.getTopicList());
                            break;
                        case "inspect":
                            if (parts.length > 1) {
                                topic = parts[1];
                                if(rsc.startInspection(topic)){
                                    System.out.println("Open interactive session on topic " + topic 
                                    		+ "\nAvailable commands: listall, delete <id>, end");
                                    inspect = true;
                                } else
                                    System.out.println("Error: no existing topic with this name");
                            } else
                                System.out.println("No key");
                            break;
                        default:
                            System.out.println("Unknown cmd");
                            break;
                    }
                } else {
                    String request = userInput.nextLine();
                    System.out.println("Server request: " + request);
                    String[] parts = request.split(" ");
                    switch (parts[0]) {
                        case "end":
                            rsc.endInspection(topic);
                            inspect = false;
                            System.out.println("Closed interactive session on topic " + topic );
                            break;
                        case "listall":
                            System.out.println(rsc.getMessagesList(topic));
                            break;
                        case "delete":
                            if (parts.length > 1) {
                                try{
                                    String IDtext = parts[1];
                                    int ID = Integer.parseInt(IDtext);
                                    rsc.deleteMessage(ID,topic);
                                } catch (Exception e) {
                                    System.out.println("Invalid ID format");
                                }
                            } else
                                System.out.println("No ID");
                            break;
                        default:
                            System.out.println("Unknown cmd");
                            break;
                    }
                }
            }

            try {
                listenerThread.interrupt();
                listenerThread.join();
            } catch (InterruptedException e) {
                return;
            }

            System.out.println("Server thread terminated");
            
        } catch (IOException e) {
            System.err.println("Connection denied - Check port");
        } finally {
            userInput.close();
        }
    }

}
