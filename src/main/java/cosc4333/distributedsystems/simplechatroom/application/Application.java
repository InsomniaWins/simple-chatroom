package cosc4333.distributedsystems.simplechatroom.application;

import cosc4333.distributedsystems.simplechatroom.Main;
import cosc4333.distributedsystems.simplechatroom.application.server.ServerApplication;

import java.net.Socket;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Application {

    private final AtomicBoolean RUNNING = new AtomicBoolean(false);
    private final Queue<Runnable> MAIN_THREAD_INSTRUCTION_QUEUE = new ConcurrentLinkedQueue<>();

    private final CommandProcessingRunnable COMMAND_RUNNABLE;
    private final Thread COMMAND_THREAD;

    // called every loop iteration while the server is active and running
    protected abstract void loop();

    // called after the application stops
    protected abstract void onApplicationStopped();

    // called when the application starts
    protected abstract void onApplicationStarted();

    // processes a user-inputted command
    // MUST CALL ON COMMAND THREAD
    public abstract void processCommand(String[] commandArray);

    public Application() {

        COMMAND_RUNNABLE = new CommandProcessingRunnable();
        COMMAND_THREAD = new Thread(COMMAND_RUNNABLE);
        COMMAND_THREAD.setName("Command");

    }

    private void processQueuedMainThreadInstructions() {

        Iterator<Runnable> iterator = MAIN_THREAD_INSTRUCTION_QUEUE.iterator();
        while (iterator.hasNext()) {
            Runnable instruction = iterator.next();
            instruction.run();
            iterator.remove();
        }

    }

    // start program
    // RUN ON MAIN THREAD
    public void start() {

        // make sure "stop" method is executed when program is closed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
            onApplicationStopped();
        }));

        RUNNING.set(true);
        COMMAND_THREAD.start();
        onApplicationStarted();


        while (isRunning()) {

            loop();
            processQueuedMainThreadInstructions();

        }

        onApplicationStopped();
    }

    // stop program
    // Thread-Safe :)
    public void stop() {

        RUNNING.set(false);

        if (COMMAND_THREAD.isAlive()) {
            COMMAND_RUNNABLE.stop();
        }

    }

    public boolean isRunning() {
        return RUNNING.get();
    }


    // queue some code to run on the main thread
    // Thread-Safe :)
    public void queueMainThreadInstruction(Runnable instruction) {
        MAIN_THREAD_INSTRUCTION_QUEUE.offer(instruction);
    }


    // called on main thread when socket could no longer be read from
    // MUST BE CALLED ON MAIN THREAD
    public void onSocketDisconnected(Socket socket) {



    }
}
