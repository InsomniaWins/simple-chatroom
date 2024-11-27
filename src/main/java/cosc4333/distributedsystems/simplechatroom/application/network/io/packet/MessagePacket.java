package cosc4333.distributedsystems.simplechatroom.application.network.io.packet;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.chatroom.ChatRoom;
import cosc4333.distributedsystems.simplechatroom.application.client.ClientApplication;
import cosc4333.distributedsystems.simplechatroom.application.network.client.ServerInformation;
import cosc4333.distributedsystems.simplechatroom.application.network.server.ClientInformation;
import cosc4333.distributedsystems.simplechatroom.application.server.ServerApplication;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagePacket extends Packet {

    private final String MESSAGE;
    private final String SENDER;

    // use when sending message from client
    public MessagePacket(String message) {
        SENDER = null;
        MESSAGE = message;
    }

    // use when relaying message to clients from server
    public MessagePacket(String sender, String message) {
        SENDER = sender;
        MESSAGE = message;
    }


    public String getMessage() {
        return MESSAGE;
    }

    public static MessagePacket deserialize(String data) {

        LinkedList<String> dataList = new LinkedList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(data);
        while (m.find()) {
            dataList.add(m.group(1).replaceAll("\"", ""));
        }

        String message = null;
        String sender = null;

        while (!dataList.isEmpty()) {
            String field = dataList.poll();
            String value = dataList.poll();

            switch (field) {
                case "message" -> {
                    message = value;

                }

                case "sender" -> {
                    sender = value;

                }
            }


        }

        if (sender != null) {
            return new MessagePacket(sender, message);
        }

        return new MessagePacket(message);
    }

    public String getSender() {
        return SENDER;
    }

    @Override
    public String serialize() {


        if (getSender() != null) {
            return "message \"" + getMessage() + "\" sender \"" + getSender() + "\"";
        }

        return "message \"" + getMessage() + "\"";
    }


    private void executeOnServer(Socket clientSocket) {

        ServerApplication server = (ServerApplication) Main.getApplication();

        server.queueMainThreadInstruction( () -> {
            ClientInformation clientInformation = server.getClient(clientSocket.getPort());

            ChatRoom chatRoom = clientInformation.getConnectedChatRoom();

            List<Socket> socketsInChatRoom = chatRoom.getConnectedSockets();

            String senderName = String.valueOf(clientSocket.getPort());

            for (Socket joinedSocket : socketsInChatRoom) {

                server.queuePacket(new MessagePacket(senderName, getMessage()), joinedSocket);

            }
        });
    }

    private void executeOnClient(Socket serverSocket) {

        ClientApplication client = (ClientApplication) Main.getApplication();

        if (client.getServerInformation() == null) return;

        ServerInformation serverInformation = client.getServerInformation();


        if (serverInformation.getConnectedChatRoom() == null) return;

        ChatRoom chatRoom = serverInformation.getConnectedChatRoom();


        if (SENDER == null) return;

        System.out.println("[" + chatRoom.getName() + "] : " + SENDER + " > " + getMessage());



    }


    @Override
    public void execute(Socket senderSocket) {

        if (Main.getApplication().isServer()) {
            executeOnServer(senderSocket);
        } else {
            executeOnClient(senderSocket);
        }


    }
}
