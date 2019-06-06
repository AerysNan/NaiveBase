package client;

import global.Global;

import java.io.*;
import java.util.Scanner;
import org.apache.commons.cli.*;

class Client extends ClientBasic {

    private Client(String ip, int writePort, int readPort) throws IOException {
        super(ip, writePort, readPort);
        Scanner sc = new Scanner(System.in);
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = Global.encrypt(sc.nextLine());
        write(username + " " + password);
        String response = read();
        if (response.startsWith("OK"))
            System.out.println("Logged in succeeded!");
        else {
            System.out.println(response.trim());
            clean();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        CommandLine cmd = argsCheck(args);
        if (cmd == null) return;
        try {
            String[] config = cmd.getOptionValue("s", "localhost:8080").split(":");
            new Client(config[0], Integer.parseInt(config[1]), Integer.parseInt(cmd.getOptionValue('p', "8081")));
        } catch (IOException e) {
            System.err.println("Failed to start client! Error message: " + e.getMessage());
            System.exit(-1);
        }
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(">");
            String message = sc.nextLine();
            if (message.isEmpty())
                continue;
            if (message.contains("import")) {
                String request = importSQLText(message);
                if (request.length() == 0) continue;
                else message = request;
            }
            write(message.trim());
            String response = read();
            System.out.println(response.trim());
            if (response.contains("Quited."))
                break;
        }
        sc.close();
        clean();
    }
}