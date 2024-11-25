package cosc4333.distributedsystems.simplechatroom.application.network.io;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.network.NetworkRunnable;
import cosc4333.distributedsystems.simplechatroom.application.server.ServerApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class InputNetworkRunnable extends NetworkRunnable {

    private final BufferedReader INPUT_READER;

    public InputNetworkRunnable(Socket socket) {
        super(socket);

        // make input buffer
        try {
            INPUT_READER = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
        } catch (IOException e) {
            Main.getLogger().severe("Failed to get input stream for socket: " + SOCKET);
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
                Main.getLogger().severe("Failed to get input from input reader of socket: " + SOCKET +
                        e.toString());
                continue;
            }

            // socket could not send data because socket is now closed
            if (input == null) {

                ServerApplication serverApplication = (ServerApplication) Main.getApplication();
                stop();
                serverApplication.queueMainThreadInstruction(() -> {
                    serverApplication.onClientDisconnected(SOCKET);
                });

            }

        }

        try {
            INPUT_READER.close();
        } catch (IOException e) {
            Main.getLogger().severe("Failed to close input stream for socket: " + SOCKET);
            throw new RuntimeException(e);
        }
    }
}
