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

            TopicHandler rsc = new TopicHandler();

            Thread listenerThread = new Thread ( new SocketListener(server, rsc) );
            listenerThread.start();

            boolean closed = false;
            while (!closed) {
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
                        System.out.println("Iteractive session");
                        break;
                    default:
                        System.out.println("Unknown cmd");
                        break;
                }
            }

            System.exit(0);

        } catch (IOException e) {
            System.err.println("IOException caught: " + e);
            e.printStackTrace();
        }finally {
            userInput.close();
        }
    }

}
