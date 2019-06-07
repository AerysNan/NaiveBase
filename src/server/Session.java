package server;

import global.Global;

import java.io.*;
import java.net.Socket;

public class Session extends Thread {
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket readSocket;
    private Socket writeSocket;
    private Context context;

    Session(Socket readSocket) {
        this.readSocket = readSocket;
    }

    private String read() {
        try {
            StringBuilder message = new StringBuilder();
            String s;
            while (!(s = reader.readLine()).contains("--END--"))
                message.append(s);
            return message.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private void write(String message) {
        try {
            writer.write(message + "\n--END--");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to socket!");
        }
    }

    private boolean connect() throws IOException {
        reader = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
        String message = read();
        if (message == null)
            return false;
        String[] data = message.split(" ");
        writeSocket = new Socket(data[0], Integer.valueOf(data[1]));
        writer = new BufferedWriter(new OutputStreamWriter(writeSocket.getOutputStream()));
        System.out.println("Session built!");
        return login();
    }

    private boolean login() {
        String message = read();
        if (message == null)
            return false;
        String[] data = message.split(" ");
        String errorMessage;
        if (data.length == 2) {
            try {
                Server.manager.login(data[0], data[1]);
                write("OK");
                context = new Context(data[0], Global.adminDatabaseName);
                System.out.println("[" + context.username + "] login succeeded!");
                return true;
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
        } else
            errorMessage = "Exception: invalid login input format!";
        write(errorMessage);
        return false;
    }

    @Override
    public void run() {
        try {
            if (!connect()) {
                while (true)
                    if (login())
                        break;
            }
        } catch (Exception e) {
            System.err.println("Failed to connect to client!");
            return;
        }
        while (true) {
            String message = read();
            if (message == null) {
                System.err.println("Failed to read from client!");
                Server.manager.logout(context.username);
                return;
            }
            if (!"".equals(message))
                System.out.println("[" + context.username + "] " + message.replaceAll(";", ";\n").trim());
            long startTime = System.currentTimeMillis();
            String result = Server.evaluator.evaluate(message, context);
            long endTime = System.currentTimeMillis();
            String time = "\nTime Cost: " + (endTime - startTime) + "ms.";
            write(result.trim() + time);
            if (result.contains("Quited.")) {
                Server.manager.logout(context.username);
                break;
            }
        }
        try {
            clean();
        } catch (IOException e) {
            System.err.println("Failed to disconnect!");
        }
    }

    private void clean() throws IOException {
        reader.close();
        readSocket.close();
        writer.close();
        writeSocket.close();
    }
}