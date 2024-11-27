package cosc4333.distributedsystems.simplechatroom.application.network.io.packet;

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
            dataList.add(m.group(1));
        }

        String roomName = null;

        while (!dataList.isEmpty()) {
            String field = dataList.poll();
            String value = dataList.poll();

            switch (field) {
                case "roomName" -> {
                    roomName = value;
                }
            }
        }

        return new JoinRoomPacket(roomName);
    }

    @Override
    public String serialize() {
        return "roomName \"" + roomName + "\"";
    }

    @Override
    public void execute() {







    }


}
