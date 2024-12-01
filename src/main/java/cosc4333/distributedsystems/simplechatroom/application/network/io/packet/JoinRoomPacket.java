package cosc4333.distributedsystems.simplechatroom.application.network.io.packet;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.Application;
import cosc4333.distributedsystems.simplechatroom.application.chatroom.ChatRoom;
import cosc4333.distributedsystems.simplechatroom.application.chatroom.ChatRoomManager;
import cosc4333.distributedsystems.simplechatroom.application.client.ClientApplication;
import cosc4333.distributedsystems.simplechatroom.application.network.client.ServerInformation;
import cosc4333.distributedsystems.simplechatroom.application.network.server.ClientInformation;
import cosc4333.distributedsystems.simplechatroom.application.server.ServerApplication;

import java.net.Socket;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinRoomPacket extends Packet {


    public String roomName = "";

    public JoinRoomPacket(String roomName) {
        this.roomName = roomName;
    }

    public static JoinRoomPacket deserialize(String data) {

        LinkedList<String> dataList = new LinkedList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(data);
        while (m.find()) {
            dataList.add(m.group(1).replaceAll("\"", ""));
        }

        String roomName = null;

        while (!dataList.isEmpty()) {
            String field = dataList.poll();
            String value = dataList.poll();

            if (field.equals("roomName")) {
                roomName = value;
            }
        }

        return new JoinRoomPacket(roomName);
    }

    @Override
    public String serialize() {
        return "roomName \"" + roomName + "\"";
    }

    // called on client after server adds the client to a chat room
    private void executeOnClient(Socket serverSocket) {

        ClientApplication client = (ClientApplication) Main.getApplication();

        client.queueMainThreadInstruction(() -> {

            Main.getLogger().info("You have joined room: " + roomName + "!");
            ServerInformation serverInformation =  client.getServerInformation();

            if (serverInformation == null) {
                Main.getLogger().severe("Failed to access server information when joining room!");
                return;
            }

            serverInformation.setConnectedChatRoom(new ChatRoom(roomName, null));

        });


    }

    // called on server when client wants to join room
    private void executeOnServer(Socket clientsocket) {

        ServerApplication server = (ServerApplication) Main.getApplication();

        server.queueMainThreadInstruction(() -> {
            ChatRoomManager roomManager = server.getChatRoomManager();

            ChatRoom chatRoom = roomManager.getOrCreateChatRoom(roomName);
            chatRoom.connectSocket(clientsocket);

            ClientInformation clientInformation = server.getClient(clientsocket.getPort());
            clientInformation.setConnectedChatRoom(chatRoom);

            server.queuePacket(this, clientsocket);

        });

    }

    @Override
    public void execute(Socket senderSocket) {



        Application application = Main.getApplication();

        if (application.isServer()) {
            executeOnServer(senderSocket);
        } else {
            executeOnClient(senderSocket);
        }

    }


}
