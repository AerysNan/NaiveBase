import parser.Evaluator;
import schema.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static BufferedReader reader;
    private static BufferedWriter writer;

    private Server(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server starts, listening port " + port + "...");
        Socket readSocket = serverSocket.accept();
        reader = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
        String message = read();
        String[] data = message.split(" ");
        Socket writeSocket = new Socket(data[0], Integer.valueOf(data[1]));
        writer = new BufferedWriter(new OutputStreamWriter(writeSocket.getOutputStream()));
        System.out.println("Connection built successfully!");
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
            Server server = new Server(8080);
            Evaluator evaluator = new Evaluator();
            while (true) {
                String message = server.read();
                System.out.println(message);
                evaluator.evaluate(message);
                if (message.equals("quit")) {
                    server.write("Goodbye!");
                    break;
                }
                server.write("client.Client said: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}