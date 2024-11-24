package cosc4333.distributedsystems.simplechatroom.application.server;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.Application;
import cosc4333.distributedsystems.simplechatroom.network.server.ClientInformation;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerApplication extends Application {

    private final ConcurrentHashMap<Integer, ClientInformation> CLIENTS;
    private final Thread SERVER_CONNECTIONS_THREAD;
    private final ServerConnectionsRunnable SERVER_CONNECTIONS_RUNNABLE;
    private final ServerSocket SERVER_SOCKET;

    public ServerApplication(String port) {

        Main.getLogger().info("Starting server on port: " + port + " . . .");

        int portInt = Integer.parseInt(port);

        try {

            SERVER_SOCKET = new ServerSocket(portInt);

        } catch (IOException e) {
            Main.getLogger().severe("Failed to start server!");
            throw new RuntimeException(e);
        }

        CLIENTS = new ConcurrentHashMap<>();

        // begin thread which handles connections to server
        // on separate thread to prevent blocking the main thread when waiting for connections
        SERVER_CONNECTIONS_RUNNABLE = new ServerConnectionsRunnable(this, SERVER_SOCKET);
        SERVER_CONNECTIONS_THREAD = new Thread(SERVER_CONNECTIONS_RUNNABLE);
        SERVER_CONNECTIONS_THREAD.setName("Server-Network");
        SERVER_CONNECTIONS_THREAD.start();
    }

    @Override
    protected void loop() {



    }

    @Override
    protected void onApplicationStopped() {

        // tell server to stop listening for connections
        SERVER_CONNECTIONS_RUNNABLE.stop();

        // interrupt current listening (the server is currently listening for a connection, so tell it to stop)
        try {
            SERVER_SOCKET.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // finished closing server
        Main.getLogger().info("Server closed.");

    }

    @Override
    protected void onApplicationStarted() {




    }

    @Override
    public void processCommand(String[] commandArray) {

    }
}
