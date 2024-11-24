package cosc4333.distributedsystems.simplechatroom.application.server;

import cosc4333.distributedsystems.simplechatroom.Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerConnectionsRunnable implements Runnable {

    private ServerSocket serverSocket;
    private ServerApplication serverApplication;
    private final AtomicBoolean RUNNING = new AtomicBoolean(false);

    public ServerConnectionsRunnable(ServerApplication serverApplication, ServerSocket serverSocket) {
        this.serverApplication = serverApplication;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {

        RUNNING.set(true);

        // while server is running, listen for client connections
        while (serverApplication.isRunning() && isRunning()) {

            Socket clientSocket;

            try {

                 clientSocket = serverSocket.accept();

            }
            // failed to accept a client socket, restart while-loop to attempt accepting another
            catch (IOException e) {

                // if the connection was not purposefully interrupted, print conneciton error
                if (isRunning()) {
                    Main.getLogger().severe("Failed to connect with client socket: " + e);
                }
                // else, the connection was purposefully interrupted, so we acknowledge that
                else {
                    Main.getLogger().info("Server is no longer listening for connections.");
                }


                continue;

            }

        }

    }

    public boolean isRunning() {
        return RUNNING.get();
    }

    public void stop() {

        RUNNING.set(false);

    }

}
