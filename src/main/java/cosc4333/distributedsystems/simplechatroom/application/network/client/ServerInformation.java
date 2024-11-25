package cosc4333.distributedsystems.simplechatroom.application.network.client;

import cosc4333.distributedsystems.simplechatroom.application.network.io.InputNetworkRunnable;
import cosc4333.distributedsystems.simplechatroom.application.network.io.OutputNetworkRunnable;

import java.net.Socket;

public class ServerInformation {

    private final Socket SOCKET;
    private final InputNetworkRunnable INPUT_RUNNABLE;
    private final OutputNetworkRunnable OUTPUT_RUNNABLE;

    public ServerInformation(Socket socket, InputNetworkRunnable inputRunnable, OutputNetworkRunnable outputRunnable) {

        SOCKET = socket;
        INPUT_RUNNABLE = inputRunnable;
        OUTPUT_RUNNABLE = outputRunnable;

    }

    public Socket getSocket() {
        return SOCKET;
    }

    public InputNetworkRunnable getInputRunnable() {
        return INPUT_RUNNABLE;
    }

    public OutputNetworkRunnable getOutputRunnable() {
        return OUTPUT_RUNNABLE;
    }

}
