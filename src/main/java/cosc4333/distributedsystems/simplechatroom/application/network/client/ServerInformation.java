package cosc4333.distributedsystems.simplechatroom.application.network.client;

import cosc4333.distributedsystems.simplechatroom.application.chatroom.ChatRoom;
import cosc4333.distributedsystems.simplechatroom.application.network.io.InputNetworkRunnable;
import cosc4333.distributedsystems.simplechatroom.application.network.io.OutputNetworkRunnable;

import java.net.Socket;

public class ServerInformation {

    private final Socket SOCKET;
    private final InputNetworkRunnable INPUT_RUNNABLE;
    private final OutputNetworkRunnable OUTPUT_RUNNABLE;

    // the chat room you (the client) are currently joined with
    private ChatRoom connectedChatRoom = null;

    public ServerInformation(Socket socket, InputNetworkRunnable inputRunnable, OutputNetworkRunnable outputRunnable) {

        SOCKET = socket;
        INPUT_RUNNABLE = inputRunnable;
        OUTPUT_RUNNABLE = outputRunnable;

    }

    // MUST RUN ON MAIN THREAD
    public void setConnectedChatRoom(ChatRoom connectedChatRoom) {
        this.connectedChatRoom = connectedChatRoom;
    }

    // MUST RUN ON MAIN THREAD
    public ChatRoom getConnectedChatRoom() {
        return connectedChatRoom;
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
