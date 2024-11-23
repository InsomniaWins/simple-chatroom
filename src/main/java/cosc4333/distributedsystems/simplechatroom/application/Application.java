package cosc4333.distributedsystems.simplechatroom.application;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Application {

    private AtomicBoolean running = new AtomicBoolean(false);

    protected abstract void loop();

    // start program
    // RUN ON MAIN THREAD
    public void start() {
        running.set(true);

        while (running.get()) {

            loop();

        }
    }

    // stop program
    // Thread-Safe :)
    public void stop() {
        running.set(false);
    }

}
