package cosc4333.distributedsystems.simplechatroom.application.client;

import cosc4333.distributedsystems.simplechatroom.application.Application;

public class ClientApplication extends Application {

    public ClientApplication(String ip, String port) {

        System.out.println("Starting client on ip: " + ip + " and port: " + port + " . . .");

    }

    @Override
    protected void loop() {

    }

    @Override
    protected void onApplicationStopped() {

    }

    @Override
    protected void onApplicationStarted() {

    }
}
