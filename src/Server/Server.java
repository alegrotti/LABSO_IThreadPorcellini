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
            /*
             * Differenza tra ServerSocket e Socket: il primo è usato per aspettare,
             * passivamente, connessioni dall'esterno; il secondo è usato per connettersi
             * attivamente ad un servizio in ascolto in un certo host, su una certa porta.
             * 
             * 
             * server.accept() rimane in attesa di una richiesta di connessione; se questa
             * arriva e va a buon fine (i.e. non solleva eccezioni), restituisce un socket
             * che verrà utilizzato per comunicare con questo specifico client.
             * 
             */
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
                            break;
                        case "show":
                            System.out.println(rsc.getTopicList());
                            break;
                        case "inspect":
                            if (parts.length > 1) {
                                topic = parts[1];
                                if(rsc.containsTopic(topic)){
                                    System.out.println("Open interactive session on topic " + topic 
                                    		+ "\nAvailable commands: listall, delete <id>, end");
                                    rsc.startInspection(topic); // inizia la sessione interattiva
                                    inspect = true;
                                }
                            } else {
                                System.out.println("No key");
                            }

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
                            rsc.endInspection(); // termina la sessione interattiva
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
                            } else {
                                System.out.println("No ID");
                            }
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
            System.err.println("IOException caught: " + e);
            e.printStackTrace();
        } finally {
            userInput.close();
        }
    }

}
