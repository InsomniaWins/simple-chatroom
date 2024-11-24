package cosc4333.distributedsystems.simplechatroom.application.client;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.Application;
import java.net.Socket;

public class ClientApplication extends Application {

    private Socket clientSocket;
    private final Thread CLIENT_CONNECTION_THREAD;
    private final ClientConnectionRunnable CLIENT_CONNECTION_RUNNABLE;

    public ClientApplication(String ip, String port) {

        Main.getLogger().info("Starting client on ip: " + ip + " and port: " + port + " . . .");

        CLIENT_CONNECTION_RUNNABLE = new ClientConnectionRunnable(ip, Integer.parseInt(port));
        CLIENT_CONNECTION_THREAD = new Thread(CLIENT_CONNECTION_RUNNABLE);
        CLIENT_CONNECTION_THREAD.setName("Client-Network");
        CLIENT_CONNECTION_THREAD.start();


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
        Socket clientSocket = CLIENT_CONNECTION_RUNNABLE.getClientSocket();
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

}
