package cosc4333.distributedsystems.simplechatroom;

import cosc4333.distributedsystems.simplechatroom.application.Application;
import cosc4333.distributedsystems.simplechatroom.application.client.ClientApplication;
import cosc4333.distributedsystems.simplechatroom.application.server.ServerApplication;

import java.util.logging.Logger;

public class Main {

    private static final Thread MAIN_THREAD = Thread.currentThread();
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

            if (args.length < 2) {
                logIncorrectUsage();
                return;
            }

            String port = args[1];

            application = new ServerApplication(port);


        } else if (programType.equals("client")) {

            if (args.length < 3) {
                logIncorrectUsage();
                return;
            }

            String ip = args[1];
            String port = args[2];

            application = new ClientApplication(ip, port);

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


    @SuppressWarnings("unused")
    public static Application getApplication() {
        return application;
    }

    public static Thread getMainThread() {
        return MAIN_THREAD;
    }
}
