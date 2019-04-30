import chat.App;
import chat.Server;


public class ServerApp {
    private final Server server;


    public static void main(String[] args) {
        ServerApp app = new ServerApp(new Server(App.DEFAULT_PORT));

        app.run();
    }


    private ServerApp(Server server) {
        this.server = server;
    }

    private void run() {
        this.server.run();
    }
}