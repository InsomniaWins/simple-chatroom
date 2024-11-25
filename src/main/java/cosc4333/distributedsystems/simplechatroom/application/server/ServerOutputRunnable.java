package cosc4333.distributedsystems.simplechatroom.application.server;

import cosc4333.distributedsystems.simplechatroom.Main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerOutputRunnable implements Runnable {

    private final AtomicBoolean RUNNING = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<String> OUTPUT_QUEUE = new ConcurrentLinkedQueue<>();
    private final Socket CLIENT_SOCKET;
    private final PrintWriter OUTPUT_WRITER;


    public ServerOutputRunnable(Socket clientSocket) {

        CLIENT_SOCKET = clientSocket;

        // make output buffer
        try {
            OUTPUT_WRITER = new PrintWriter(CLIENT_SOCKET.getOutputStream(), true);
        } catch (IOException e) {
            Main.getLogger().severe("Failed to get output stream for client: " + CLIENT_SOCKET);
            throw new RuntimeException(e);
        }

        RUNNING.set(true);
    }

    @Override
    public void run() {

        while (Main.getApplication().isRunning() && isRunning() ) {

            Iterator<String> outputIterator = OUTPUT_QUEUE.iterator();
            while (outputIterator.hasNext()) {
                String message = outputIterator.next();
                outputIterator.remove();

                OUTPUT_WRITER.println(message);
            }


        }

        OUTPUT_WRITER.close();
    }

    // thread-safe
    public boolean isRunning() {
        return RUNNING.get();
    }

    // thread-safe
    public void stop() {
        RUNNING.set(false);
    }

    // queues a string message to be sent to the client socket
    // thread-safe :)
    public void queueMessage(String message) {

        OUTPUT_QUEUE.offer(message);

    }
}
