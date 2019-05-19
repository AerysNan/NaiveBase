import parser.Evaluator;

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

    private String read() {
        try {
            StringBuilder message = new StringBuilder();
            String s;
            while (!(s = reader.readLine()).contains("--END--"))
                message.append(s);
            return message.toString();
        } catch (IOException e) {
            System.err.println("Failed to read from client! Error message: " + e.getMessage());
            return "";
        }
    }

    private void write(String message) {
        try {
            writer.write(message + "--END--");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to socket! Error message: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = null;
        try {
            server = new Server(8080);
        } catch (IOException e) {
            System.err.println("Failed to start server! Error message: " + e.getMessage());
            System.exit(-1);
        }
        Evaluator evaluator = null;
        try {
            evaluator = new Evaluator();
        } catch (Exception e) {
            System.err.println("Failed to start evaluator! Error message: " + e.getMessage());
            System.exit(-1);
        }
        while (true) {
            String message = server.read();
            if (!"".equals(message))
                System.out.println(message.trim());
            String result = evaluator.evaluate(message);
            server.write(result);
            if (result.contains("Quited."))
                break;
        }
    }
}