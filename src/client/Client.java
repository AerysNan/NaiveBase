package client;

import global.Global;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static ServerSocket serverSocket;
    private static Socket readSocket;
    private static Socket writeSocket;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    private Client(String ip, int port) throws IOException {
        writeSocket = new Socket(ip, port);
        writer = new BufferedWriter(new OutputStreamWriter(writeSocket.getOutputStream()));
        write("localhost 8081");
        serverSocket = new ServerSocket(8081);
        readSocket = serverSocket.accept();
        reader = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
        System.out.println("Successfully connected to server!");
        Scanner sc = new Scanner(System.in);
        System.out.println("username: ");
        String username = sc.nextLine();
        System.out.println("password: ");
        String password = Global.encrypt(sc.nextLine());
        write(username + " " + password);
        if (read().startsWith("OK"))
            System.out.println("Successfully logged in!");
        else {
            System.out.println("Invalid username or password!");
            clean();
            System.exit(0);
        }
    }

    private static String read() {
        try {
            StringBuilder message = new StringBuilder();
            String s;
            while (!(s = reader.readLine()).contains("--END--"))
                message.append(s).append('\n');
            return message.toString();
        } catch (IOException e) {
            System.err.println("Failed to read from server! Error message: " + e.getMessage());
            return "";
        }
    }

    private static void write(String message) {
        try {
            writer.write(message + "\n--END--");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to socket! Error message: " + e.getMessage());
        }
    }

    private static String importSQLText(String command) {
        String[] data = command.replaceAll(";", "").split(" ");
        String fileName;
        if (data.length <= 1) {
            System.err.println("Failed to parse import command!");
            return "";
        }
        fileName = data[1].trim();

        File file = new File(fileName);
        StringBuilder message = new StringBuilder();
        String s;
        try {
            BufferedReader lineReader = new BufferedReader(new FileReader(file));
            while ((s = lineReader.readLine()) != null)
                message.append(s).append('\n');
            lineReader.close();
            return message.toString();
        } catch (IOException e) {
            System.err.println("Failed to open sql file! Error message: " + e.getMessage());
            return "";
        }
    }

    private static void clean() {
        try {
            reader.close();
            readSocket.close();
            writer.close();
            writeSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket! Error message: " + e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        try {
            new Client("localhost", 8080);
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