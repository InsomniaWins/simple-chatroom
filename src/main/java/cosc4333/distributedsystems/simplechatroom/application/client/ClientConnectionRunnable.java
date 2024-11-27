package cosc4333.distributedsystems.simplechatroom.application.client;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.network.client.ServerInformation;
import cosc4333.distributedsystems.simplechatroom.application.network.io.InputNetworkRunnable;
import cosc4333.distributedsystems.simplechatroom.application.network.io.OutputNetworkRunnable;
import cosc4333.distributedsystems.simplechatroom.application.network.server.ClientInformation;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnectionRunnable implements Runnable {

    private final String IP;
    private final int PORT;
    private final AtomicBoolean RUNNING = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<Runnable> CLIENT_CONNECTION_THREAD_INSTRUCTION_QUEUE = new ConcurrentLinkedQueue<>();
    private final ClientApplication CLIENT_APPLICATION;

    // each thread handles communication with server
    private final ExecutorService serverThreadPool = Executors.newCachedThreadPool();

    private Socket serverSocket;

    public ClientConnectionRunnable(ClientApplication clientApplication, String ip, int port) {
        CLIENT_APPLICATION = clientApplication;
        IP = ip;
        PORT = port;
        serverSocket = null;
    }

    // RUN ON CLIENT NETWORK THREAD
    protected void disconnect() {

        // check if already disconnected
        if (serverSocket == null || !serverSocket.isConnected() || serverSocket.isClosed()) {
            Main.getLogger().info("Cannot disconnect from server because client is not connected to begin with!");
            return;
        }

        // attempt disconnection
        try {

            serverSocket.close();

        } catch (IOException e) {

            Main.getLogger().severe("Failed to disconnect from server!\nPlease run \"disconnect\" to retry disconnection.");
            return;

        }

        serverSocket = null;
        ServerInformation serverInformation = CLIENT_APPLICATION.getServerInformation();

        if (serverInformation != null) {

            InputNetworkRunnable inputRunnable = serverInformation.getInputRunnable();
            OutputNetworkRunnable outputRunnable = serverInformation.getOutputRunnable();

            inputRunnable.stop();
            outputRunnable.stop();
        }

        Main.getLogger().info("Disconnected from server!");

    }

    // RUN ON CLIENT NETWORK THREAD
    protected void attemptConnection() {

        // check if already connected
        if (serverSocket != null) {
            if (serverSocket.isConnected()) {
                Main.getLogger().info("Cannot connect to server because client is already connected to a server!");
                return;
            }
        }

        // attempt connection
        try {

            serverSocket = new Socket(IP, PORT);
            Main.getLogger().info("Connected to server!");

        } catch (IOException e) {

            Main.getLogger().severe("Failed to connect to server!\nPlease run \"connect\" to retry connection.");
            return;

        }

        // make input and output buffers
        OutputNetworkRunnable outputRunnable = new OutputNetworkRunnable(serverSocket);
        InputNetworkRunnable inputRunnable = new InputNetworkRunnable(serverSocket);

        CLIENT_APPLICATION.setServerInformation(new ServerInformation(serverSocket, inputRunnable, outputRunnable));

        serverThreadPool.submit(outputRunnable);
        serverThreadPool.submit(inputRunnable);

    }

    public boolean isRunning() {
        return RUNNING.get();
    }

    public void stop() {

        RUNNING.set(false);

    }

    private void processQueuedInstructions() {

        Iterator<Runnable> iterator = CLIENT_CONNECTION_THREAD_INSTRUCTION_QUEUE.iterator();
        while (iterator.hasNext()) {
            Runnable instruction = iterator.next();
            instruction.run();
            iterator.remove();
        }

    }

    @Override
    public void run() {

        RUNNING.set(true);

        attemptConnection();

        while (Main.getApplication().isRunning() && isRunning()) {

            processQueuedInstructions();

        }

        serverThreadPool.close();
    }

    // thread-safe :)
    public void queueClientConnectionThreadInstruction(Runnable instruction) {
        CLIENT_CONNECTION_THREAD_INSTRUCTION_QUEUE.offer(instruction);
    }

    public Socket getServerSocket() {
        return serverSocket;
    }


    // called on main thread when server socket could no longer be read from
    // (assume this is called after input buffer and input thread has already been closed)
    // thread-safe, but should NOT be called directly
    protected void onServerDisconnected(Socket serverSocket) {

        ServerInformation serverInformation = CLIENT_APPLICATION.getServerInformation();

        if (serverInformation == null) {
            // failed to get client information
            Main.getLogger().severe("Failed to get server information for server: " + serverSocket);
            return;
        }


        // make sure input is stopped

        InputNetworkRunnable inputRunnable = serverInformation.getInputRunnable();

        if (inputRunnable != null && inputRunnable.isRunning()) {
            inputRunnable.stop();
        }



        // stop output runnable

        OutputNetworkRunnable outputRunnable = serverInformation.getOutputRunnable();

        if (outputRunnable != null && outputRunnable.isRunning()) {
            outputRunnable.stop();
        }



        // remove client information from server
        CLIENT_APPLICATION.setServerInformation(null);
        stop();

    }

}
