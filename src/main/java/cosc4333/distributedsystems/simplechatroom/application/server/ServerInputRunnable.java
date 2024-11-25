package cosc4333.distributedsystems.simplechatroom.application.server;

import cosc4333.distributedsystems.simplechatroom.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerInputRunnable implements Runnable {

    private final AtomicBoolean RUNNING = new AtomicBoolean(false);
    private final Socket CLIENT_SOCKET;
    private final BufferedReader INPUT_READER;


    public ServerInputRunnable(Socket clientSocket) {

        CLIENT_SOCKET = clientSocket;

        // make output buffer
        try {
            INPUT_READER = new BufferedReader(new InputStreamReader(CLIENT_SOCKET.getInputStream()));
        } catch (IOException e) {
            Main.getLogger().severe("Failed to get input stream for client: " + CLIENT_SOCKET);
            throw new RuntimeException(e);
        }

        RUNNING.set(true);
    }

    @Override
    public void run() {

        while (Main.getApplication().isRunning() && isRunning() ) {

            String input = null;

            try {
                input = INPUT_READER.readLine();
            } catch (IOException e) {
                Main.getLogger().severe("Failed to get input from input reader of client: " + CLIENT_SOCKET +
                        e.toString());
                continue;
            }

            // socket could not send data because socket is now closed
            if (input == null) {

                ServerApplication serverApplication = (ServerApplication) Main.getApplication();
                stop();
                serverApplication.queueMainThreadInstruction(() -> {
                    serverApplication.onClientDisconnected(CLIENT_SOCKET);
                });

            }

        }

        try {
            INPUT_READER.close();
        } catch (IOException e) {
            Main.getLogger().severe("Failed to close input stream for client: " + CLIENT_SOCKET);
            throw new RuntimeException(e);
        }

    }

    // thread-safe
    public boolean isRunning() {
        return RUNNING.get();
    }

    // thread-safe
    public void stop() {
        RUNNING.set(false);
    }

}
