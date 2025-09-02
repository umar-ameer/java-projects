import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private final int port;
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Chat server starting on port " + port + "...");
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket socket = server.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                pool.execute(handler);
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /** Broadcast to everyone EXCEPT the sender */
    private void broadcastToOthers(String message, ClientHandler sender) {
        for (ClientHandler ch : clients) {
            if (ch != sender) {
                ch.send(message);
            }
        }
    }

    private void remove(ClientHandler ch) {
        clients.remove(ch);
    }

    private void shutdown() {
        for (ClientHandler ch : clients) ch.close();
        pool.shutdownNow();
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String name = "Guest";

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                out.println("Enter your name:");
                String n = in.readLine();
                if (n != null && !n.isBlank()) name = n.trim();

                System.out.println(name + " joined from " + socket.getRemoteSocketAddress());
                broadcastToOthers("[Server] " + name + " joined the chat.", this);
                out.println("Welcome, " + name + ". Type /quit to exit.");

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("/quit")) break;

                    String msg = name + ": " + line;

                    // Log on server
                    System.out.println(msg);

                    // Send to others
                    broadcastToOthers(msg, this);

                    // Echo back to the sender so they also see their own message
                    out.println(msg);
                }
            } catch (IOException ignored) {
            } finally {
                close();
                remove(this);
                broadcastToOthers("[Server] " + name + " left the chat.", this);
                System.out.println(name + " disconnected.");
            }
        }

        void send(String msg) {
            if (out != null) out.println(msg);
        }

        void close() {
            try { if (in != null) in.close(); } catch (IOException ignored) {}
            if (out != null) out.close();
            try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        }
    }

    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 6000;
        new ChatServer(port).start();
    }
}