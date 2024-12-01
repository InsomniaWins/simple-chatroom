package cosc4333.distributedsystems.simplechatroom.application.chatroom;

import cosc4333.distributedsystems.simplechatroom.Main;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatRoom {

    private final String NAME;
    private final ConcurrentLinkedQueue<Socket> CONNECTED_SOCKETS = new ConcurrentLinkedQueue<>();
    private final ChatRoomManager CHAT_ROOM_MANAGER;

    public ChatRoom(String name, ChatRoomManager chatRoomManager) {
        NAME = name;
        CHAT_ROOM_MANAGER = chatRoomManager;
    }

    // thread-safe :)
    public String getName() {
        return NAME;
    }

    // thread-safe :)
    public void disconnectSocket(Socket socket) {
        CONNECTED_SOCKETS.remove(socket);


        // if no clients connected to room, free room from memory
        if (CONNECTED_SOCKETS.isEmpty()) {

            // only need to free from memory on server, because clients do not have ChatRoomManager objects
            if (Main.getApplication().isServer()) {
                CHAT_ROOM_MANAGER.deleteChatRoom(NAME);
            }

        }

    }

    // thread-safe :)
    public void connectSocket(Socket socket) {
        CONNECTED_SOCKETS.add(socket);
    }

    public List<Socket> getConnectedSockets() {
        return CONNECTED_SOCKETS.stream().toList();
    }

}
