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
//            Manager db = new Manager();
//            Column col1 = new Column("id", Type.INT, true);
//            Column col2 = new Column("name", Type.STRING, false);
//            Column col3 = new Column("score", Type.DOUBLE, false);
//            Column[] columns = new Column[]{col1, col2, col3};
//            Database txy = db.createDatabase("txy");
//            Database ny = db.createDatabase("ny");
//            Table table = txy.createTable("grade", columns);
//            Entry[] e1 = new Entry[]{
//                    new Entry(0, 1, table),
//                    new Entry(1, "A", table),
//                    new Entry(2, 47.56, table)
//            };
//            Entry[] e2 = new Entry[]{
//                    new Entry(0, 2, table),
//                    new Entry(1, "B", table),
//                    new Entry(2, 56.43, table)
//            };
//            Entry[] e3 = new Entry[]{
//                    new Entry(0, 3, table),
//                    new Entry(1, "C", table),
//                    new Entry(2, 76.51, table)
//            };
//            table.insert(e1);
//            table.insert(e2);
//            table.insert(e3);
//            db.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}