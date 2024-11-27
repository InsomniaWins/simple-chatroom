package cosc4333.distributedsystems.simplechatroom.application.util;

import java.util.List;

public class Command {

    public static final int IMPROPER_USAGE = -1;
    public static final int SUCCESS = 0;


    public final String USAGE;
    public final String DESCRIPTION;
    public final ICommandBody BODY;

    public Command(String usage, String description, ICommandBody body) {
        USAGE = usage;
        DESCRIPTION = description;
        BODY = body;
    }


    public void execute(List<String> commandParameters) {
        BODY.execute(commandParameters);
    }

    @Override
    public String toString() {
        return "\tusage: " + USAGE + "\n\tdescription: " + DESCRIPTION;
    }
}
