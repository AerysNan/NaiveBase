package server;

import org.apache.commons.cli.*;
import parser.Evaluator;
import schema.Manager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket serverSocket;
    static Evaluator evaluator;
    static Manager manager;

    private Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        try {
            manager = new Manager();
            evaluator = new Evaluator(manager);
        } catch (Exception e) {
            System.err.println("Failed to start evaluator! Error message: " + e.getMessage());
            System.exit(-1);
        }
        System.out.println("Server starts, listening port " + port + "...");
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("p")
                .argName("PORT")
                .longOpt("server")
                .desc("server listen port")
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
            helpFormatter.printHelp("java -jar server.jar", "", options, "");
            return;
        }
        try {
            new Server(Integer.valueOf(cmd.getOptionValue("p", "8080")));
        } catch (IOException e) {
            System.err.println("Failed to start server!");
            System.exit(-1);
        }
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Failed to connect to client!");
                continue;
            }
            Session session = new Session(socket);
            session.start();
        }
    }
}