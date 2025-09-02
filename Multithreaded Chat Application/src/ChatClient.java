import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private final String host;
    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (Socket socket = new Socket(host, port);
             BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out    = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             Scanner stdin      = new Scanner(System.in)) {

            // Reader thread: prints everything from server
            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ignored) {
                }
                System.out.println("Disconnected from server.");
                System.exit(0);
            });
            reader.setDaemon(true);
            reader.start();

            // First prompt from server will ask for name; just forward user input
            while (true) {
                String user = stdin.nextLine();
                out.println(user);
                if ("/quit".equalsIgnoreCase(user)) break;
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port    = args.length > 1 ? Integer.parseInt(args[1]) : 6000;
        new ChatClient(host, port).start();
    }
}