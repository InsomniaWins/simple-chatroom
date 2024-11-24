package cosc4333.distributedsystems.simplechatroom.application;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.client.ClientApplication;
import cosc4333.distributedsystems.simplechatroom.application.server.ServerApplication;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandProcessingRunnable implements Runnable {


    private final AtomicBoolean RUNNING = new AtomicBoolean(false);


    // RUN ON COMMAND THREAD
    private void processCommand(String command) {

        String[] commandArray = command.split(" ");
        String commandName = commandArray[0];

        switch (commandName) {
            case "stop", "close", "quit", "exit" -> {
                stop();
                Main.getApplication().stop();
            }
            default -> {

                if (Main.getApplication() instanceof ClientApplication clientApplication) {

                    clientApplication.processCommand(commandArray);

                } else if (Main.getApplication() instanceof ServerApplication serverApplication) {

                    serverApplication.processCommand(commandArray);

                }

            }
        }

    }

    public boolean isRunning() {
        return RUNNING.get();
    }

    public void stop() {
        RUNNING.set(false);
    }

    @Override
    public void run() {

        RUNNING.set(true);

        // make scanner to listen for command input
        Scanner inputScanner = new Scanner(System.in);

        // listen for commands from user terminal
        while (Main.getApplication().isRunning() && isRunning()) {

            // get command
            String command = inputScanner.nextLine();

            // process command
            processCommand(command);

        }


    }

}
