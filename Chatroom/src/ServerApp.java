import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private static ArrayList<String> usernames = new ArrayList<>();
    private static ArrayList<ClientThread> clients = new ArrayList<ClientThread>();

    public static void main(String[] args) {
        System.out.println("Chat server is running...");
        try (ServerSocket serverSocket = new ServerSocket(6968)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);
                ClientThread newClient = new ClientThread(socket);
                clients.add(newClient);
                newClient.start();
            }

        } catch (Exception e) {
            System.out.println("Error in the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientThread excludeClient) {
        System.out.println(message);
        for (ClientThread client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);

            }
        }
    }


    public static void addUsername(String username) throws Exception {
        usernames.add(username);
        writeClientsToDatabase(username);
    }

    public static void removeUser(String username, ClientThread client) {
        boolean removed = usernames.remove(username);
        if (removed) {
            clients.remove(client);
            System.out.println("The user " + username + " has left the chat");
        }
    }

    public static void writeMessageInDataBase(String message) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/chatroom_v2", "erf", "4013613056");
        String sqlCom="INSERT INTO messages (messagesText) VALUES ('"+message+"')";
        Statement statement=connection.prepareStatement(sqlCom);
        statement.execute(sqlCom);
        connection.close();
    }
    //----------------------------------------------------------------
    public static void readMessagesFromDatabase(ClientThread clientThread) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/chatroom_v2", "erf", "4013613056");
        String sqlCom="select * FROM messages";
        Statement s=connection.prepareStatement(sqlCom);
        ResultSet rs=s.executeQuery(sqlCom);
        while(rs.next())
        {
            clientThread.sendMessage(rs.getString("messagesText"));
        }
        connection.close();
    }
    //----------------------------------------------------------------
    public static void writeClientsToDatabase(String username) throws Exception
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/chatroom_v2", "erf", "4013613056");
        String sqlCom="INSERT INTO clients (Name) VALUES ('"+username+"')";
        Statement statement=connection.prepareStatement(sqlCom);
        statement.execute(sqlCom);
        connection.close();
    }

    //----------------------------------------------------------------
    public static int readClientsFromDatabase(String username) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/chatroom_v2", "erf", "4013613056");
        String sqlCom="select * FROM clients";
        Statement s=connection.prepareStatement(sqlCom);
        ResultSet rs=s.executeQuery(sqlCom);
        while(rs.next())
        {
            if (rs.getString("Name").equals(username))
            {
                connection.close();
                return 1;
            }
        }
        connection.close();
        return 0;
    }

    //----------------------------------------------------------------
    public static ArrayList<String> getUsernames() {
        return usernames;
    }

}