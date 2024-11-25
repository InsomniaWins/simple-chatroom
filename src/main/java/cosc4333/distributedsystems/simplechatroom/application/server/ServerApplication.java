package cosc4333.distributedsystems.simplechatroom.application.server;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.Application;
import cosc4333.distributedsystems.simplechatroom.network.server.ClientInformation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    }

    public void removeClientInformation(int clientPort) {
        CLIENTS.remove(clientPort);
    }

    public void setClient(int clientPort, ClientInformation clientInformation) {
        CLIENTS.put(clientPort, clientInformation);
    }

    public ClientInformation getClient(int clientPort) {
        return CLIENTS.get(clientPort);
    }

    public ConcurrentHashMap<Integer, ClientInformation> getClients() {
        return CLIENTS;
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

        SERVER_CONNECTIONS_THREAD.start();
        Main.getLogger().info("Started server!");


    }

    @Override
    public void processCommand(String[] commandArray) {

    }

    // called on main thread when client socket could no longer be read from
    // MUST BE CALLED ON MAIN THREAD
    protected void onClientDisconnected(Socket clientSocket) {

        SERVER_CONNECTIONS_RUNNABLE.onClientDisconnected(clientSocket);
        Main.getLogger().info("Client disconnected: " + clientSocket);

    }


}
