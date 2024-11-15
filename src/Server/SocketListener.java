package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class SocketListener implements Runnable {
	
    ServerSocket server;
    TopicHandler rsc;
    ArrayList<Thread> children = new ArrayList<>();

    public SocketListener(ServerSocket server, TopicHandler rsc) {
        this.server = server;
        this.rsc = rsc;
    }

    @Override
    public void run() {
        try {
            this.server.setSoTimeout(5000);
            while (!Thread.interrupted()) {
                try {
                    Socket s = this.server.accept();
                    if (!Thread.interrupted()) {
                        System.out.println("Client connected");

                        Thread handlerThread = new Thread( new ClientHandler(s, rsc) );

                        handlerThread.setName("Client");

                        handlerThread.start();

                        this.children.add(handlerThread);
                    } else {
                        s.close();
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    continue;
                } catch (IOException e) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("[SocketListener] IOException caught: "+e.getMessage());
        } finally {
            System.out.println("Closing server and interrupting client handlers...");
            try {
                this.server.close();
            } catch (IOException e) {
                System.err.println("[SocketListener] Error closing server socket: "+e.getMessage());
            }

            System.out.println("Interrupting children...");
            for (Thread child : this.children) {
                System.out.println("\t- Interrupting " + child.getName() + "...");
                child.interrupt();
            }
        }
        System.out.println("SocketListener terminated.");
    }
}
