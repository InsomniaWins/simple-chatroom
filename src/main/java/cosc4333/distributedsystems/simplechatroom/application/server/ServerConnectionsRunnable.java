package cosc4333.distributedsystems.simplechatroom.application.server;

import cosc4333.distributedsystems.simplechatroom.Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionsRunnable implements Runnable {

    private ServerSocket serverSocket;
    private ServerApplication serverApplication;

    public ServerConnectionsRunnable(ServerApplication serverApplication, ServerSocket serverSocket) {
        this.serverApplication = serverApplication;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {


        // while server is running, listen for client connections
        while (serverApplication.isRunning()) {

            Socket clientSocket;

            try {

                 clientSocket = serverSocket.accept();

            }
            // failed to accept a client socket, restart while-loop to attempt accepting another
            catch (IOException e) {

                Main.getLogger().severe("Failed to connect with client socket: " + e);
                continue;

            }



        }

    }



}
