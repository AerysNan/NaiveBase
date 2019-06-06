package client;

import org.apache.commons.cli.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class ClientBasic {
    private static ServerSocket serverSocket;
    private static Socket readSocket;
    private static Socket writeSocket;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    ClientBasic(String ip, int writePort, int readPort) throws IOException {
        writeSocket = new Socket(ip, writePort);
        writer = new BufferedWriter(new OutputStreamWriter(writeSocket.getOutputStream()));
        write("localhost " + readPort);
        serverSocket = new ServerSocket(readPort);
        readSocket = serverSocket.accept();
        reader = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
        System.out.println("Successfully connected to server!");
    }

    static String read() {
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

    static void write(String message) {
        try {
            writer.write(message + "\n--END--");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to socket! Error message: " + e.getMessage());
        }
    }

    static void clean() {
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

    static String importSQLText(String command) {
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

    static CommandLine argsCheck(String[] args) {
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
            return null;
        }
        return cmd;
    }
}