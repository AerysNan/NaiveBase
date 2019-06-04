import parser.Evaluator;
import schema.Session;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static ServerSocket serverSocket;
    private static Socket readSocket;
    private static Socket writeSocket;
    private static Evaluator evaluator;
    private static Session session;

    private Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        try {
            session = new Session();
            evaluator = new Evaluator(session);
        } catch (Exception e) {
            System.err.println("Failed to start evaluator! Error message: " + e.getMessage());
            System.exit(-1);
        }
        System.out.println("Server starts, listening port " + port + "...");
    }

    private static boolean connect() throws IOException {
        readSocket = serverSocket.accept();
        reader = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
        String message = read();
        String[] data = message.split(" ");
        writeSocket = new Socket(data[0], Integer.valueOf(data[1]));
        writer = new BufferedWriter(new OutputStreamWriter(writeSocket.getOutputStream()));
        System.out.println("Connection built successfully!");
        message = read();
        data = message.split(" ");
        if (session.login(data[0], data[1])) {
            System.out.println("Login successfully!");
            write("OK");
            return true;
        } else {
            System.out.println("Login failed!");
            write("Failed");
            return false;
        }
    }

    private static void clean() throws IOException {
        reader.close();
        readSocket.close();
        writer.close();
        writeSocket.close();
    }

    private static String read() {
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

    private static void write(String message) {
        try {
            writer.write(message + "\n--END--");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to socket! Error message: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            new Server(8080);
        } catch (IOException e) {
            System.err.println("Failed to start server! Error message: " + e.getMessage());
            System.exit(-1);
        }
        while (true) {
            try {
                if (!connect()) {
                    clean();
                    continue;
                }
            } catch (Exception e) {
                System.err.println("Failed to connect to client! Error message: " + e.getMessage());
                System.exit(-1);
            }
            while (true) {
                String message = read();
                if (!"".equals(message))
                    System.out.println(message.replaceAll(";", ";\n").trim());
                long startTime = System.currentTimeMillis();
                String result = evaluator.evaluate(message);
                long endTime = System.currentTimeMillis();
                String time = "\nTime Cost: " + (endTime - startTime) + "ms.";
                write(result.trim() + time);
                if (result.contains("Quited."))
                    break;
            }
            try {
                clean();
            } catch (Exception e) {
                System.err.println("Failed to close socket! Error message: " + e.getMessage());
                System.exit(-1);
            }
        }
    }
}