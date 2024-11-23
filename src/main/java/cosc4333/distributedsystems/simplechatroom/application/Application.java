package cosc4333.distributedsystems.simplechatroom.application;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Application {

    private AtomicBoolean running = new AtomicBoolean(false);

    protected abstract void loop();
    protected abstract void onApplicationStopped();
    protected abstract void onApplicationStarted();

    // start program
    // RUN ON MAIN THREAD
    public void start() {

        running.set(true);
        onApplicationStarted();

        while (isRunning()) {

            loop();

        }
        onApplicationStopped();
    }

    // stop program
    // Thread-Safe :)
    public void stop() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }


}
