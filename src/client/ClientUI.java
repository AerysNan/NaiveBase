package client;

import javax.swing.UIManager;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;

class ClientUI extends ClientBasic {

    private ClientUI(String ip, int writePort, int readPort) throws IOException {
        super(ip, writePort, readPort);
    }

    public static void main(String[] args) {
        CommandLine cmd = Client.argsCheck(args);
        if (cmd == null) return;
        try {
            String[] config = cmd.getOptionValue("s", "localhost:8080").split(":");
            new ClientUI(config[0], Integer.parseInt(config[1]), Integer.parseInt(cmd.getOptionValue('p', "8082")));
        } catch (IOException e) {
            System.err.println("Failed to start client! Error message: " + e.getMessage());
            System.exit(-1);
        }
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Failed to set client stylesheet! Error message: " + e.getMessage());
            System.exit(-1);
        }
        new ClientUILogin();
    }
}