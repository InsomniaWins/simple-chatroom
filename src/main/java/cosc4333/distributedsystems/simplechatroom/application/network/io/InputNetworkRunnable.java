package cosc4333.distributedsystems.simplechatroom.application.network.io;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.Application;
import cosc4333.distributedsystems.simplechatroom.application.network.NetworkRunnable;
import cosc4333.distributedsystems.simplechatroom.application.network.io.packet.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class InputNetworkRunnable extends NetworkRunnable {

    private final BufferedReader INPUT_READER;

    public InputNetworkRunnable(Socket socket) {
        super(socket);

        // make input buffer
        try {
            INPUT_READER = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
        } catch (IOException e) {
            Main.getLogger().severe("Failed to get input stream for socket: " + SOCKET);
            throw new RuntimeException(e);
        }

        RUNNING.set(true);
    }


    @Override
    public void run() {
        while (Main.getApplication().isRunning() && isRunning() ) {

            String input = null;

            try {
                input = INPUT_READER.readLine();
            } catch (IOException e) {
                Main.getLogger().severe("Failed to get input from input reader of socket: " + SOCKET +
                        e.toString());
                continue;
            }

            // socket could not send data because socket is now closed
            if (input == null) {

                Application application = Main.getApplication();
                stop();
                application.queueMainThreadInstruction(() -> {
                    application.onSocketDisconnected(SOCKET);
                });

                return;
            }

            // check for packet and reconstruct packet object
            if (input.charAt(0) == '[') {
                StringBuilder classNameBuilder = new StringBuilder();
                String packetData = "";
                for (int i = 0; i < input.length() - 1; i++) {
                    char currentChar = input.charAt(i + 1);

                    if (currentChar == ']') {
                        packetData = input.substring(currentChar);
                        break;
                    }

                    classNameBuilder.append(currentChar);
                }

                String className = classNameBuilder.toString();
                if (!className.startsWith("packet=")) {
                    // data is not valid packet format
                    return;
                }

                // remove "packet=" prefix from className to obtain JUST the packet's class name
                className = classNameBuilder.replace(0, 7, "").toString();


                Class<?> dataClass = null;
                try {
                    dataClass = Class.forName(className.toString());

                } catch (ClassNotFoundException e) {

                    // class type does not exist, therefore sender likely did not attempt to send a packet
                    // or sent data with an invalid packet class
                    return;
                }


                // check if class is child class of IPacket interface
                if (Packet.class.isAssignableFrom(dataClass)) {

                    Class<? extends Packet> packetClass = dataClass.asSubclass(Packet.class);

                    // make object of class
                    Method deserializeMethod = null;
                    try {

                        deserializeMethod = packetClass.getMethod("deserialize", String.class);

                    } catch (NoSuchMethodException e) {
                        Main.getLogger().severe("Failed to find deserialize method for packet class: " + packetClass);
                        return;
                    }

                    Packet packet = null;

                    try {
                        packet = (Packet) deserializeMethod.invoke(null, packetData);
                    } catch (Exception e) {
                        Main.getLogger().severe("Failed to reconstruct received packet: " + packetClass);
                        return;
                    }

                    packet.execute();

                }
            }



        }

        try {
            INPUT_READER.close();
        } catch (IOException e) {
            Main.getLogger().severe("Failed to close input stream for socket: " + SOCKET);
            throw new RuntimeException(e);
        }
    }
}
