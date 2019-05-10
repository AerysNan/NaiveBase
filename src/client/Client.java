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

    private String read() throws IOException {
        return reader.readLine();
    }

    private void write(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            Client client = new Client("localhost", 8080);
            while (true) {
                System.out.print(">");
                String message = sc.nextLine();
                client.write(message);
                String response = client.read();
                System.out.println(response);
                if (response.equals("Goodbye!"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}