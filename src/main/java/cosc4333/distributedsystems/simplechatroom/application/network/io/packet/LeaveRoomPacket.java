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

public class LeaveRoomPacket extends Packet {


    public static LeaveRoomPacket deserialize(String data) {
        return new LeaveRoomPacket();
    }

    @Override
    public String serialize() {
        return "";
    }

    // called on client after server adds the client to a chat room
    private void executeOnClient(Socket serverSocket) {

        ClientApplication client = (ClientApplication) Main.getApplication();
        ServerInformation serverInformation = client.getServerInformation();

        if (serverInformation == null) return;

        serverInformation.setConnectedChatRoom(null);

        System.out.println("You have left the room.");

    }

    // called on server when client wants to join room
    private void executeOnServer(Socket clientsocket) {

        ServerApplication server = (ServerApplication) Main.getApplication();

        server.queueMainThreadInstruction(() -> {

            ClientInformation clientInformation = server.getClient(clientsocket.getPort());

            if (clientInformation == null) return;

            ChatRoom chatRoom = clientInformation.getConnectedChatRoom();

            if (chatRoom == null) return;

            chatRoom.disconnectSocket(clientsocket);
            clientInformation.setConnectedChatRoom(null);

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
