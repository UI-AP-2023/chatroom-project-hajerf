import java.io.*;
import java.net.Socket;
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
            InputStream input = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            out = new PrintWriter(output, true);
            while (true) {
                out.println("Connection was successful");
                username = in.readLine();
                if (username.isEmpty()) {
                    continue;
                }
                synchronized (ServerApp.getUsernames()){
                    if (!ServerApp.getUsernames().contains(username)) {   ////rem
                        ServerApp.addUsername(username);
                        break;
                    }}

            }
            out.println("Username is valid");

             ServerApp.broadcast(username + " has joined the chat", this);
            //Receive on the server and send to the clients
            while (true){
            String massage= in.readLine();
            if(massage.equals("Exit")){
                out.println("Exit");
                break;
            }
            if(massage.equals(" ")){
                out.println("Connected");
            }else{
            ServerApp.broadcast(username + ": " + massage, excludeClient);}
            }

            ServerApp.removeUser(username, this);
            socket.close();
            ServerApp.broadcast(username + " has left the chat", this);
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }


}
