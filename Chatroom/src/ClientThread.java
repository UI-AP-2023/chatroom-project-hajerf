import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class ClientThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private ClientThread excludeClient;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        excludeClient=this;
        try {
            //String str=readMessagesFromDatabase();
            InputStream input = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            out = new PrintWriter(output, true);
            ///////////sign up
            while (true) {
                out.println("Connection was successful");
                username = in.readLine();
                if (username.isEmpty()) {
                    continue;
                }
                synchronized (ServerApp.getUsernames()){
                        int result=ServerApp.readClientsFromDatabase(username);
                        if (result==0)
                        {
                            ServerApp.addUsername(username);
                            break;
                        }
                    }

            }
            out.println("Username is valid");
            ///////complete sign up
            ServerApp.broadcast(username + " has joined the chat", this);
            //////////// first ping
            if( in.readLine().equals(" ")){
                out.println("Connected");}
            ServerApp.readMessagesFromDatabase(this);
            while (true){
                String massage= in.readLine();
                if(massage.equals("Exit")){
                    out.println("Exit");
                    break;
                }
                if(massage.equals(" ")){
                    out.println("Connected");
                }else{
                    ServerApp.broadcast(username + ": " + massage, excludeClient);
                    ServerApp.writeMessageInDataBase(username + ": " + massage);

                }

            }
            ServerApp.removeUser(username, this);
            socket.close();
            ServerApp.broadcast(username + " has left the chat", this);
        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
        }
    }




    public void sendMessage(String message) {
        out.println(message);
    }



}
