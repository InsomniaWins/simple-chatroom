package cosc4333.distributedsystems.simplechatroom.application.network.io;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.network.NetworkRunnable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OutputNetworkRunnable extends NetworkRunnable {

    private final ConcurrentLinkedQueue<String> OUTPUT_QUEUE = new ConcurrentLinkedQueue<>();
    private final PrintWriter OUTPUT_WRITER;

    public OutputNetworkRunnable(Socket socket) {
        super(socket);

        // make output buffer
        try {
            OUTPUT_WRITER = new PrintWriter(SOCKET.getOutputStream(), true);
        } catch (IOException e) {
            Main.getLogger().severe("Failed to get output stream for socket: " + SOCKET);
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

    // queues a string message to be sent to the socket
    // thread-safe :)
    public void queueMessage(String message) {

        OUTPUT_QUEUE.offer(message);

    }
}
