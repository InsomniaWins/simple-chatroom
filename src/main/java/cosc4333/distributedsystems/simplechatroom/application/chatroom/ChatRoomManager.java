package cosc4333.distributedsystems.simplechatroom.application.chatroom;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {


    private final ConcurrentHashMap<String, ChatRoom> CHAT_ROOMS = new ConcurrentHashMap<>();

    public ChatRoomManager() {

    }

    // thread-safe :)
    public Collection<ChatRoom> getChatRooms() {
        return CHAT_ROOMS.values();
    }

    // thread-safe :)
    public void deleteChatRoom(String roomName) {

        CHAT_ROOMS.remove(roomName);

    }

    // thread-safe :)
    public ChatRoom getChatRoom(String roomName) {
        return CHAT_ROOMS.get(roomName);
    }

    // thread-safe :)
    public ChatRoom getOrCreateChatRoom(String roomName) {

        ChatRoom chatRoom = getChatRoom(roomName);

        // check if room already exists
        if (chatRoom != null) {

            // return already existing room
            return chatRoom;
        }

        // create new chat room and add to map
        chatRoom = new ChatRoom(roomName, this);
        CHAT_ROOMS.put(roomName, chatRoom);

        // return chat room
        return chatRoom;
    }


}
