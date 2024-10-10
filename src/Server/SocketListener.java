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
            this.server.close();
        } catch (IOException e) {
            System.err.println("SocketListener: IOException caught: " + e);
            e.printStackTrace();
        }

        System.out.println("Interrupting children...");
        for (Thread child : this.children) {
            System.out.println("Interrupting " + child.getName() + "...");
            child.interrupt();
        }
    }
}
