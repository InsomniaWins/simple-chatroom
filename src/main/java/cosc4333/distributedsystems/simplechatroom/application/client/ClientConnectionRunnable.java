package cosc4333.distributedsystems.simplechatroom.application.client;

import cosc4333.distributedsystems.simplechatroom.Main;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnectionRunnable implements Runnable {

    private final String IP;
    private final int PORT;
    private Socket clientSocket;
    private final AtomicBoolean RUNNING = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<Runnable> CLIENT_CONNECTION_THREAD_INSTRUCTION_QUEUE = new ConcurrentLinkedQueue<>();

    public ClientConnectionRunnable(String ip, int port) {
        IP = ip;
        PORT = port;
        clientSocket = null;
    }

    // RUN ON CLIENT NETWORK THREAD
    protected void disconnect() {

        // check if already disconnected
        if (clientSocket == null || !clientSocket.isConnected() || clientSocket.isClosed()) {
            Main.getLogger().info("Cannot disconnect from server because client is not connected to begin with!");
            return;
        }

        // attempt disconnection
        try {

            clientSocket.close();
            clientSocket = null;

            Main.getLogger().info("Disconnected from server!");

        } catch (IOException e) {

            Main.getLogger().severe("Failed to disconnect from server!\nPlease run \"disconnect\" to retry disconnection.");

        }

    }

    // RUN ON CLIENT NETWORK THREAD
    protected void attemptConnection() {

        // check if already connected
        if (clientSocket != null) {
            if (clientSocket.isConnected()) {
                Main.getLogger().info("Cannot connect to server because client is already connected to a server!");
                return;
            }
        }

        // attempt connection
        try {

            clientSocket = new Socket(IP, PORT);
            Main.getLogger().info("Connected to server!");




        } catch (IOException e) {

            Main.getLogger().severe("Failed to connect to server!\nPlease run \"connect\" to retry connection.");

        }
    }

    public boolean isRunning() {
        return RUNNING.get();
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

    }

    // thread-safe :)
    public void queueClientConnectionThreadInstruction(Runnable instruction) {
        CLIENT_CONNECTION_THREAD_INSTRUCTION_QUEUE.offer(instruction);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
