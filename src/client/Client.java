package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static BufferedReader reader;
    private static BufferedWriter writer;

    private Client(String ip, int port) throws IOException {
        Socket writeSocket = new Socket(ip, port);
        writer = new BufferedWriter(new OutputStreamWriter(writeSocket.getOutputStream()));
        write("localhost 8081\n");
        ServerSocket serverSocket = new ServerSocket(8081);
        Socket readSocket = serverSocket.accept();
        reader = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
        System.out.println("Successfully connected to server!");
    }

    private String read() {
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

    private void write(String message) {
        try {
            writer.write(message + "\n--END--");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to socket! Error message: " + e.getMessage());
        }
    }

    private String importSQLText(String command) {
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

    public static void main(String[] args) {
        Client client = null;
        try {
            client = new Client("localhost", 8080);
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
                String request = client.importSQLText(message);
                if (request.length() == 0) continue;
                else message = request;
            }
            client.write(message.trim());
            String response = client.read();
            System.out.println(response.trim());
            if (response.contains("Quited."))
                break;
        }
        sc.close();
    }
}