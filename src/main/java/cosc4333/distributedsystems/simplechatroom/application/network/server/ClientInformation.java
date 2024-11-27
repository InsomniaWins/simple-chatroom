package cosc4333.distributedsystems.simplechatroom.application.network.server;

import cosc4333.distributedsystems.simplechatroom.application.chatroom.ChatRoom;
import cosc4333.distributedsystems.simplechatroom.application.network.io.InputNetworkRunnable;
import cosc4333.distributedsystems.simplechatroom.application.network.io.OutputNetworkRunnable;

import java.net.Socket;

// generic class to hold information of a specific client
public class ClientInformation {

    private final Socket SOCKET;
    private final InputNetworkRunnable INPUT_RUNNABLE;
    private final OutputNetworkRunnable OUTPUT_RUNNABLE;

    private ChatRoom connectedChatRoom = null;

    public ClientInformation(Socket socket, InputNetworkRunnable inputRunnable, OutputNetworkRunnable outputRunnable) {
        SOCKET = socket;
        INPUT_RUNNABLE = inputRunnable;
        OUTPUT_RUNNABLE = outputRunnable;
    }

    // thread-safe :)
    public synchronized ChatRoom getConnectedChatRoom() {
        return connectedChatRoom;
    }

    // thread-safe :)
    public synchronized void setConnectedChatRoom(ChatRoom connectedChatRoom) {
        this.connectedChatRoom = connectedChatRoom;
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

    @Override
    public String toString() {
        return "client information: { socket: " + getSocket() + ", connected chat room: " + getConnectedChatRoom() + " }";
    }
}
