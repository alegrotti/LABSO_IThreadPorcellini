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
                    to.println(request);
                    if (request.equals("quit")) {
                        break;
                    }
                } else {
                    userInput.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("IOException caught: " + e);
        } finally {
            System.out.println("Sender closed.");
            userInput.close();
        }
    }
}
