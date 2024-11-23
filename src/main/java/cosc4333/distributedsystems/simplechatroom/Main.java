package cosc4333.distributedsystems.simplechatroom;

import cosc4333.distributedsystems.simplechatroom.application.Application;
import cosc4333.distributedsystems.simplechatroom.application.ClientApplication;
import cosc4333.distributedsystems.simplechatroom.application.ServerApplication;

import java.util.logging.Logger;

public class Main {


    private static final Logger LOGGER = Logger.getLogger("Main Logger");
    private static Application application;

    public static void main(String[] args) {

        if (args.length < 1) {

            LOGGER.severe("Please specify either client or server!");
            logIncorrectUsage();

            return;

        }

        String programType = args[0];


        if (programType.equals("server")) {

            application = new ServerApplication();

        } else if (programType.equals("client")) {

            application = new ClientApplication();

        } else {

            logIncorrectUsage();
            return;

        }

        application.start();

    }

    private static void logIncorrectUsage() {
        LOGGER.info("Incorrect usage! Please use one of the following:\n" + getUsageString());
    }

    public static String getUsageString() {

        // creates server for clients to connect to
        return  "Server Usage: java -jar simple-chatroom.jar server [port]\n" +

        // creates client which connects to server (joining/leaving chatrooms happens AFTER connection to server is established
                "Client Usage: java -jar simple-chatroom.jar client [ip] [port]";
    }


    public static Logger getLogger() {
        return LOGGER;
    }

    public static Application getApplication() {
        return application;
    }
}
