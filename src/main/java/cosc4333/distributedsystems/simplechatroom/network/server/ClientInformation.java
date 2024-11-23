package cosc4333.distributedsystems.simplechatroom.network.server;

import java.net.Socket;

public class ClientInformation {

    private final Socket SOCKET;

    public ClientInformation(Socket socket) {
        SOCKET = socket;
    }

    public Socket getSocket() {
        return SOCKET;
    }
}
