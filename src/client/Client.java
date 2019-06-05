package client;

import global.Global;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import org.apache.commons.cli.*;

public class Client {
    private static ServerSocket serverSocket;
    private static Socket readSocket;
    private static Socket writeSocket;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    private Client(String ip, int writePort, int readPort) throws IOException {
        writeSocket = new Socket(ip, writePort);
        writer = new BufferedWriter(new OutputStreamWriter(writeSocket.getOutputStream()));
        write("localhost " + readPort);
        serverSocket = new ServerSocket(readPort);
        readSocket = serverSocket.accept();
        reader = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
        System.out.println("Successfully connected to server!");
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
        Options options = new Options();
        options.addOption(Option.builder("s")
                .argName("ADDRESS")
                .longOpt("server")
                .desc("server listen address")
                .hasArg()
                .required(false)
                .build()
        );
        options.addOption(Option.builder("p")
                .argName("PORT")
                .longOpt("port")
                .desc("client read port")
                .hasArg()
                .build()
        );
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("print help information")
                .hasArg(false)
                .build()
        );
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Invalid command line argument!");
            System.exit(-1);
        }
        if (cmd.hasOption("h")) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar client.jar", "", options, "");
            return;
        }
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