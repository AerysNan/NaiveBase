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
        write("localhost 8081");
        ServerSocket serverSocket = new ServerSocket(8081);
        Socket readSocket = serverSocket.accept();
        reader = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
        System.out.println("Successfully connected to server!");
    }

    private String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.err.println("Failed to read from socket! Error message: " + e.getMessage());
            return "";
        }
    }

    private void write(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to socket! Error message: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Client client = null;
        try {
            client = new Client("localhost", 8080);
        } catch (IOException e) {
            System.err.println("Failed to start client! Error message: " + e.getMessage());
            System.exit(-1);
        }
        while (true) {
            System.out.print(">");
            String message = sc.nextLine();
            client.write(message);
            String response = client.read();
            if (!response.equals(""))
                System.out.println(response);
            if (response.equals("Quited. \n"))
                break;
        }
    }
}