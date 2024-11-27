package cosc4333.distributedsystems.simplechatroom.application.client;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.Application;
import cosc4333.distributedsystems.simplechatroom.application.chatroom.ChatRoom;
import cosc4333.distributedsystems.simplechatroom.application.network.client.ServerInformation;
import cosc4333.distributedsystems.simplechatroom.application.network.io.packet.JoinRoomPacket;
import cosc4333.distributedsystems.simplechatroom.application.network.io.packet.LeaveRoomPacket;
import cosc4333.distributedsystems.simplechatroom.application.network.io.packet.MessagePacket;
import cosc4333.distributedsystems.simplechatroom.application.util.Command;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ClientApplication extends Application {

    private final Thread CLIENT_CONNECTION_THREAD;
    private final ClientConnectionRunnable CLIENT_CONNECTION_RUNNABLE;

    private ServerInformation serverInformation;

    public ClientApplication(String ip, String port) {

        Main.getLogger().info("Starting client on ip: " + ip + " and port: " + port + " . . .");

        CLIENT_CONNECTION_RUNNABLE = new ClientConnectionRunnable(this, ip, Integer.parseInt(port));
        CLIENT_CONNECTION_THREAD = new Thread(CLIENT_CONNECTION_RUNNABLE);
        CLIENT_CONNECTION_THREAD.setName("Client-Network");

        COMMAND_MAP.put("connect", new Command(
                "connect",
                "connects to the server",
                (List<String> commandParameters) -> {
                    attemptConnectToServer();
                    return Command.SUCCESS;
                })
        );
        COMMAND_MAP.put("disconnect", new Command(
                "disconnect",
                "disconnects from the server",
                (List<String> commandParameters) -> {
                    disconnectFromServer();
                    return Command.SUCCESS;
                })
        );
        COMMAND_MAP.put("join", new Command(
                "join \"room name in quotation marks\"",
                "joins a chat room",
                (List<String> commandParameters) -> {

                    if (commandParameters.isEmpty()) {
                        return Command.IMPROPER_USAGE;
                    }

                    String roomName = commandParameters.getFirst();

                    if (serverInformation == null) {
                        Main.getLogger().info("Could not join room, because you are not connected to a server!");
                        return Command.SUCCESS;
                    }

                    JoinRoomPacket packet = new JoinRoomPacket(roomName);
                    serverInformation.getOutputRunnable().queuePacket(packet);
                    return Command.SUCCESS;
                })
        );
        COMMAND_MAP.put("leave", new Command(
                "leave",
                "leaves the chat room",
                (List<String> commandParameters) -> {
                    if (serverInformation == null) {
                        Main.getLogger().info("Could not leave room, because you are not connected to a server!");
                        return Command.SUCCESS;
                    }

                    ChatRoom chatRoom = serverInformation.getConnectedChatRoom();
                    if (chatRoom == null) {
                        Main.getLogger().info("Could not leave room, because you have not joined a chat room!");
                        return Command.SUCCESS;
                    }

                    serverInformation.getOutputRunnable().queuePacket(new LeaveRoomPacket());
                    return Command.SUCCESS;
                })
        );
        COMMAND_MAP.put("send", new Command(
                "send \"message in quotation marks\"",
                "sends a message to the chat room",
                (List<String> commandParameters) -> {

                    if (commandParameters.isEmpty()) {
                        return Command.IMPROPER_USAGE;
                    }

                    String message = commandParameters.getFirst();

                    if (serverInformation == null) {
                        Main.getLogger().info("Could not send message, because you are not connected to a server!");
                        return Command.SUCCESS;
                    }

                    if (serverInformation.getConnectedChatRoom() == null) {
                        Main.getLogger().info("Could not send message, because you have not joined a chat room!");
                        return Command.SUCCESS;
                    }

                    serverInformation.getOutputRunnable().queuePacket(new MessagePacket(message));
                    return Command.SUCCESS;
                })
        );

    }

    @Override
    protected void loop() {

    }

    @Override
    protected void onApplicationStopped() {
        Main.getLogger().info("Client closed.");
    }

    @Override
    protected void onApplicationStarted() {

        CLIENT_CONNECTION_THREAD.start();
        Main.getLogger().info("Started client!");

    }

    // thread-safe :)
    public void attemptConnectToServer() {

        // if on client network thread, attempt connection
        if (Thread.currentThread() == CLIENT_CONNECTION_THREAD) {
            CLIENT_CONNECTION_RUNNABLE.attemptConnection();

        }
        // else, tell client network thread to attempt connection
        else {

            queueClientConnectionThreadInstruction(CLIENT_CONNECTION_RUNNABLE::attemptConnection);

        }

    }

    // called before serverInformation is thrown away, but after server socket has closed
    // MUST BE CALLED ON MAIN THREAD
    protected void onDisconnectedFromServer() {

        serverInformation = null;

    }

    // thread-safe :)
    public void disconnectFromServer() {

        // if on client network thread, disconnect
        if (Thread.currentThread() == CLIENT_CONNECTION_THREAD) {
            CLIENT_CONNECTION_RUNNABLE.disconnect();
        }
        // else, tell client network thread to disconnect
        else {

            queueClientConnectionThreadInstruction(CLIENT_CONNECTION_RUNNABLE::disconnect);

        }

    }

    // thread-safe :)
    public void queueClientConnectionThreadInstruction(Runnable instruction) {
        CLIENT_CONNECTION_RUNNABLE.queueClientConnectionThreadInstruction(instruction);
    }

    // NOT THREAD SAFE (yet)
    public boolean isClientConnected() {
        Socket clientSocket = CLIENT_CONNECTION_RUNNABLE.getServerSocket();
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed() && serverInformation != null;
    }

    public ServerInformation getServerInformation() {
        return serverInformation;
    }

    public void setServerInformation(ServerInformation serverInformation) {
        this.serverInformation = serverInformation;
    }

    @Override
    public void onSocketDisconnected(Socket socket) {

        CLIENT_CONNECTION_RUNNABLE.onServerDisconnected(socket);
        Main.getLogger().info("Server disconnected: " + socket);

    }
}
