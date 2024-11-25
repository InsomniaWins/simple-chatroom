package cosc4333.distributedsystems.simplechatroom.application.client;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.Application;
import cosc4333.distributedsystems.simplechatroom.application.network.client.ServerInformation;

import java.net.Socket;

public class ClientApplication extends Application {

    private final Thread CLIENT_CONNECTION_THREAD;
    private final ClientConnectionRunnable CLIENT_CONNECTION_RUNNABLE;

    private ServerInformation serverInformation;

    public ClientApplication(String ip, String port) {

        Main.getLogger().info("Starting client on ip: " + ip + " and port: " + port + " . . .");

        CLIENT_CONNECTION_RUNNABLE = new ClientConnectionRunnable(this, ip, Integer.parseInt(port));
        CLIENT_CONNECTION_THREAD = new Thread(CLIENT_CONNECTION_RUNNABLE);
        CLIENT_CONNECTION_THREAD.setName("Client-Network");

    }

    @Override
    protected void loop() {

    }

    @Override
    protected void onApplicationStopped() {
        Main.getLogger().info("Client closed.");
    }

    @Override
    protected void onApplicationStarted() {

        CLIENT_CONNECTION_THREAD.start();
        Main.getLogger().info("Started client!");

    }

    // thread-safe :)
    public void attemptConnectToServer() {

        // if on client network thread, attempt connection
        if (Thread.currentThread() == CLIENT_CONNECTION_THREAD) {
            CLIENT_CONNECTION_RUNNABLE.attemptConnection();
        }
        // else, tell client network thread to attempt connection
        else {

            queueClientConnectionThreadInstruction(CLIENT_CONNECTION_RUNNABLE::attemptConnection);

        }

    }

    // thread-safe :)
    public void disconnectFromServer() {

        // if on client network thread, disconnect
        if (Thread.currentThread() == CLIENT_CONNECTION_THREAD) {
            CLIENT_CONNECTION_RUNNABLE.disconnect();
        }
        // else, tell client network thread to disconnect
        else {

            queueClientConnectionThreadInstruction(CLIENT_CONNECTION_RUNNABLE::disconnect);

        }

    }

    @Override
    public void processCommand(String[] commandArray) {
        String commandName = commandArray[0];

        switch (commandName) {
            case "connect" -> {

                attemptConnectToServer();

            }

            case "disconnect" -> {

                disconnectFromServer();

            }
        }
    }

    // thread-safe :)
    public void queueClientConnectionThreadInstruction(Runnable instruction) {
        CLIENT_CONNECTION_RUNNABLE.queueClientConnectionThreadInstruction(instruction);
    }

    // NOT THREAD SAFE (yet)
    public boolean isClientConnected() {
        Socket clientSocket = CLIENT_CONNECTION_RUNNABLE.getServerSocket();
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    public ServerInformation getServerInformation() {
        return serverInformation;
    }

    public void setServerInformation(ServerInformation serverInformation) {
        this.serverInformation = serverInformation;
    }

    @Override
    public void onSocketDisconnected(Socket socket) {

        CLIENT_CONNECTION_RUNNABLE.onServerDisconnected(socket);
        Main.getLogger().info("Server disconnected: " + socket);

    }
}
