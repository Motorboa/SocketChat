import chat.App;
import chat.Client;
import chat.Connection;
import chat.client_event.EventManager;
import chat.GUI;
import chat.client_event.ExitEvent;
import chat.client_event.MessageReceivedEvent;
import chat.client_event.MessageSendEvent;

import java.io.IOException;


public class ClientApp {
    private static final String APPLICATION_NAME = "Graphic Chat Client";

    private EventManager eventManager;
    private Client client;
    private GUI ui;


    public static void main(String[] args) {
        EventManager eventManager = new EventManager();

        ClientApp app = new ClientApp(
                eventManager,
                new Client(App.DEFAULT_HOST, App.DEFAULT_PORT, eventManager),
                new GUI(APPLICATION_NAME, eventManager)
        );

        app.run();
    }


    private ClientApp(EventManager eventManager, Client client, GUI ui) {
        this.eventManager = eventManager;
        this.client = client;
        this.ui = ui;
    }

    public void run() {
        Connection connection;
        try {
            connection = client.createConnection();
        } catch (IOException e) {
            System.err.println("Can not establish connection to server: " + e.getMessage());
            e.printStackTrace(System.err);

            System.exit(1);
            return;
        }

        eventManager
                .subscribe(MessageSendEvent.class, (message) -> {
                    try {
                        connection.write(message);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);

                        System.exit(2);
                    }
                })
                .subscribe(ExitEvent.class, (message) -> {
                    // we ignore received message as it is just an empty string for this type of event.
                    try {
                        connection.write(App.TERMINATE_MESSAGE);
                        connection.terminate();
                    } catch (IOException e) {
                        e.printStackTrace(System.err);

                        System.exit(3);
                    }
                })
                .subscribe(MessageReceivedEvent.class, (message) -> {
                    ui.appendNewMessage(message);
                });

        client.run(connection);

        javax.swing.SwingUtilities.invokeLater(ui);      // our GUI
    }
}