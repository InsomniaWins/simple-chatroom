package cosc4333.distributedsystems.simplechatroom.network.server;

import cosc4333.distributedsystems.simplechatroom.application.server.ServerInputRunnable;
import cosc4333.distributedsystems.simplechatroom.application.server.ServerOutputRunnable;

import java.net.Socket;

public class ClientInformation {

    private final Socket SOCKET;
    private final ServerInputRunnable INPUT_RUNNABLE;
    private final ServerOutputRunnable OUTPUT_RUNNABLE;

    public ClientInformation(Socket socket, ServerInputRunnable inputRunnable, ServerOutputRunnable outputRunnable) {
        SOCKET = socket;
        INPUT_RUNNABLE = inputRunnable;
        OUTPUT_RUNNABLE = outputRunnable;
    }

    public Socket getSocket() {
        return SOCKET;
    }

    public ServerInputRunnable getInputRunnable() {
        return INPUT_RUNNABLE;
    }

    public ServerOutputRunnable getOutputRunnable() {
        return OUTPUT_RUNNABLE;
    }
}
