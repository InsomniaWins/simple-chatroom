package cosc4333.distributedsystems.simplechatroom.application.network;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkRunnable implements Runnable {

    protected final AtomicBoolean RUNNING = new AtomicBoolean(false);
    protected final Socket SOCKET;

    public NetworkRunnable(Socket socket) {
        SOCKET = socket;
    }

    @Override
    public void run() {

    }

    // thread-safe
    public boolean isRunning() {
        return RUNNING.get();
    }

    // thread-safe
    public void stop() {
        RUNNING.set(false);
    }

}
