package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientSender implements Runnable {

    Socket s;

    public ClientSender(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        Scanner userInput = new Scanner(System.in);

        try {
            PrintWriter to = new PrintWriter(this.s.getOutputStream(), true);
            while (true) {
                
                if (!Thread.interrupted()) {

                    String request = userInput.nextLine();
                    /*
                    * se il thread è stato interrotto mentre leggevamo l'input da tastiera, inviamo
                    * "quit" al server e usciamo
                    */
                    /* in caso contrario proseguiamo e analizziamo l'input inserito */
                    to.println(request);
                    if (request.equals("quit")) {
                        break;
                    }

                } else {
                    //to.println("quit");
                    userInput.close();
                    break;
                }

            }
        } catch (IOException e) {
            System.err.println("IOException caught: " + e);
            e.printStackTrace();
        } finally {
            System.out.println("Sender closed.");
            userInput.close();
        }
    }

}
