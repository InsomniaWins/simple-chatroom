package cosc4333.distributedsystems.simplechatroom.application.server;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.network.io.InputNetworkRunnable;
import cosc4333.distributedsystems.simplechatroom.application.network.io.OutputNetworkRunnable;
import cosc4333.distributedsystems.simplechatroom.application.network.server.ClientInformation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerConnectionsRunnable implements Runnable {

    private ServerSocket serverSocket;
    private ServerApplication serverApplication;
    private final AtomicBoolean RUNNING = new AtomicBoolean(false);

    // each thread handles communication with clients
    private final ExecutorService clientThreadPool = Executors.newCachedThreadPool();

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

                Main.getLogger().info("Listening for client . . .");

                clientSocket = serverSocket.accept();

                Main.getLogger().info("Client connected: " + clientSocket);

                OutputNetworkRunnable outputRunnable = new OutputNetworkRunnable(clientSocket);
                InputNetworkRunnable inputRunnable = new InputNetworkRunnable(clientSocket);

                serverApplication.setClient(clientSocket.getPort(),
                        new ClientInformation(clientSocket, inputRunnable, outputRunnable)
                );

                clientThreadPool.submit(outputRunnable);
                clientThreadPool.submit(inputRunnable);

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

        clientThreadPool.close();
    }

    public boolean isRunning() {
        return RUNNING.get();
    }

    public void stop() {

        RUNNING.set(false);

    }


    // called on main thread when client socket could no longer be read from
    // (assume this is called after input buffer and input thread has already been closed)
    // thread-safe, but should NOT be called directly
    protected void onClientDisconnected(Socket clientSocket) {

        ClientInformation clientInformation = serverApplication.getClient(clientSocket.getPort());

        if (clientInformation == null) {
            // failed to get client information
            Main.getLogger().severe("Failed to get client information for client: " + clientSocket);
            return;
        }


        // make sure input is stopped

        InputNetworkRunnable inputRunnable = clientInformation.getInputRunnable();

        if (inputRunnable != null && inputRunnable.isRunning()) {
            inputRunnable.stop();
        }



        // stop output runnable

        OutputNetworkRunnable outputRunnable = clientInformation.getOutputRunnable();

        if (outputRunnable != null && outputRunnable.isRunning()) {
            outputRunnable.stop();
        }



        // remove client information from server
        serverApplication.removeClientInformation(clientSocket.getPort());
        stop();

    }

}
