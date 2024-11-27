package cosc4333.distributedsystems.simplechatroom.application.chatroom;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatRoom {

    private final String NAME;
    private final ConcurrentLinkedQueue<Socket> CONNECTED_SOCKETS = new ConcurrentLinkedQueue<>();

    public ChatRoom(String name) {
        NAME = name;
    }

    // thread-safe :)
    public String getName() {
        return NAME;
    }

    // thread-safe :)
    public void disconnectSocket(Socket socket) {
        CONNECTED_SOCKETS.remove(socket);
    }

    // thread-safe :)
    public void connectSocket(Socket socket) {
        CONNECTED_SOCKETS.add(socket);
    }

    public List<Socket> getConnectedSockets() {
        return CONNECTED_SOCKETS.stream().toList();
    }

}
