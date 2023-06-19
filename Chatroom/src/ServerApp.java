import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private static int port = 8989;
    private static ArrayList<String> usernames = new ArrayList<>();
    private static ArrayList<ClientThread> clients = new ArrayList<ClientThread>();

    public static void main(String[] args) {
        System.out.println("Chat server is running...");
        try (ServerSocket serverSocket = new ServerSocket(6969)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);
                ClientThread newClient = new ClientThread(socket);
               // newClient.setId(ID++);
                clients.add(newClient);
                newClient.start();
            }

        } catch (IOException e) {
            System.out.println("Error in the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientThread excludeClient) {
        for (ClientThread client : clients) {
              if (client != excludeClient) {
            client.sendMessage(message);

              }
        }
    }


    public static void addUsername(String username) {
        usernames.add(username);
    }

    public static void removeUser(String username, ClientThread client) {
        boolean removed = usernames.remove(username);
        if (removed) {
            clients.remove(client);
            System.out.println("The user " + username + " has left the chat");
        }
    }

    public static ArrayList<String> getUsernames() {
        return usernames;
    }

}