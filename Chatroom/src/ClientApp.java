import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {
    public static Scanner sc = new Scanner(System.in);
    public static Socket socket;
    public static PrintWriter out;
    public static InputStreamReader in;
    public static BufferedReader reader;
    public static long start;

    public static void main(String[] args) throws IOException, InterruptedException {
        socket = new Socket("127.0.0.1", 6968);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(in);
        if (reader.readLine().equals("Connection was successful")) {
            String result = "nothing";
            do {
                System.out.println("Enter Username:");
                String userName = sc.nextLine();
                out.println(userName);
                result = reader.readLine();
            }
            while (!result.equals("Username is valid"));
            System.out.println("Username accepted!!");

            start=System.currentTimeMillis();
            out.println(" ");
            String firstResult = reader.readLine();
            if (firstResult.contains("Connected")) {

                long end = System.currentTimeMillis();
                ping(start, end);
                ClientRead clientRead=new ClientRead();
                Thread read=new Thread(clientRead);
                read.start();
                ClientWrite clientWrite=new ClientWrite();
                Thread write=new Thread(clientWrite);
                write.start();

                write.join();
                read.join();


            }
            System.out.println("this client is disconnected from the server!!");
        }
    }


    //----------------------------------------------------------------
    static class ClientWrite implements Runnable {

        @Override
        public void run() {
            while (true) {
                String message = sc.nextLine();

                if (message.equals("Exit")) {
                    out.println(message);
                    break;
                }
                if (message.equals("Ping"))
                {
                    start=System.currentTimeMillis();
                    out.println(" ");
                }
                out.println(message);
            }
        }
    }
    //----------------------------------------------------------------
    static class ClientRead implements Runnable {

        @Override
        public void run() {

            try {
                while (true) {
                    String result = reader.readLine();
                    if (result.equals("Exit")) {
                        in.close();
                        out.close();
                        reader.close();
                        socket.close();
                        break;
                    }else if(result.equals("accept to go pv chAT")){
                        out.println("accept to go pv chAT");
                    }else{
                    System.out.println(result);}
                    if (result.contains("Connected")) {
                        long end = System.currentTimeMillis();
                        ping(start, end);
                    }

                }
            } catch (IOException e) {
                System.out.println("an error occurred while reading from server!!");
            }
        }
    }

    //----------------------------------------------------------------
    public static void ping(long start,long end )
    {
        long ping=end - start;
        System.out.println("Ping: "+ping+" milliseconds");
    }
}