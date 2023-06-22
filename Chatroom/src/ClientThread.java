import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
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
    public boolean blPvChat=false;
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
            //////////////send database
            ServerApp.readMessagesFromDatabase(this);
            while (true){
                String massage= in.readLine();
                if(massage.contains("pv")){
                    pvChat(massage);
                }else
                if(massage.equals("Exit")){
                    out.println("Exit");
                    break;
                }
                if(massage.equals(" ")){
                    out.println("Connected");
                }
                else{
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
    public ClientThread findSocketByName(String name){
        for(ClientThread client:ServerApp.GETClients()){
            if(client.username.equals(name)){
                return client;
            }
        }
        return null;
    }
    public void RequestPvChat(String pvMessage) throws IOException {
        String orderList[] = pvMessage.split(" ");
        ClientThread clientGetRequest = findSocketByName(orderList[1]);
        if(clientGetRequest!=null) {
            clientGetRequest.sendMessage(this.username + " send you a request to join pv chat do you want to join?");
            if(in.readLine().equals("accept to go pv chAT")){
                sendMessage("hizzzzz*");
                blPvChat=true;
                while (true){
                String PvMessages=in.readLine();
                if(PvMessages.contains("exit pv")){
                    clientGetRequest.sendMessage(clientGetRequest.username +"is exit pv chat");
                    blPvChat=false;

                    break;
                }
                clientGetRequest.sendMessage(this.username +" :"+PvMessages);
            }}
        }else{
        sendMessage("the person you want to communicate with is not online");}
    }
    public void getPvChat(String message) throws IOException {
        String orderList[] = message.split(" ");
        ClientThread clientSenderRequest = findSocketByName(orderList[1]);
        if(orderList[0].equals("join")){
            clientSenderRequest.sendMessage("accept to go pv chAT");
            blPvChat=true;
            while (true){
                String PvMessages=in.readLine();
                if(PvMessages.contains("exit pv")){
                    clientSenderRequest.sendMessage(clientSenderRequest.username +"is exit pv chat");
                    blPvChat=false;
                    break;
                } else {
                    clientSenderRequest.sendMessage(PvMessages);
                }
            }
        }else if(orderList[0].equals("NotJoin")){
            clientSenderRequest.sendMessage("Not accept to go pv chAT");
        }
    }

    public void pvChat(String message) throws IOException {
        if(message.contains("join")){
            getPvChat(message);
        }else{
            RequestPvChat(message);
        }
    }



    public void sendMessage(String message) {
        out.println(message);
    }



}
