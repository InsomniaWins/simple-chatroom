package cosc4333.distributedsystems.simplechatroom.application;

public class ClientApplication extends Application {

    public ClientApplication(String ip, String port) {

        System.out.println("Starting client on ip: " + ip + " and port: " + port + " . . .");

    }

    @Override
    protected void loop() {

    }
}
