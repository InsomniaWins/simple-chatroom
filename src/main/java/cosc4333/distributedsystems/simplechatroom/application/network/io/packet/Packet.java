package cosc4333.distributedsystems.simplechatroom.application.network.io.packet;


import cosc4333.distributedsystems.simplechatroom.Main;

public abstract class Packet {

    public abstract String serialize();

    // called on socket's input thread
    // therefore, if you want some code to run on main thread,
    // you must do the following in the body of the execute method:
    /*
                Main.getApplication().queueMainThreadInstruction(() -> {
                    // . . . some code . . .
                });
     */
    public abstract void execute();

    public static String getSerializedPacketPrefix(Packet packet) {
        return "[packet=" + packet.getClass().getName() + "]";

    }
}
