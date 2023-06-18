import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) throws IOException {

        Scanner sc=new Scanner(System.in);
        Socket socket =new Socket("127.0.0.1",6666);
        PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
        InputStreamReader in=new InputStreamReader(socket.getInputStream());
        BufferedReader reader=new BufferedReader(in);

        if (reader.readLine().equals("Connection was successful"))
        {
            String result="nothing";
            do {
                System.out.println("Enter Username:");
                String userName=sc.nextLine();
                out.println(userName);
                result= reader.readLine();
            }
            while (!result.equals("Username is valid"));
            System.out.println("Username accepted!!");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true)
                    {
                        String message=sc.nextLine();
                        out.println(message);
                        if (message.equals("Exit"))
                        {
                            break;
                        }
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true)
                    {
                        try {
                            String result= reader.readLine();
                            if (result.equals("Exit"))
                            {
                                break;
                            }
                            System.out.println(result);
                        } catch (IOException e) {
                            System.out.println("an error occured while reading from server!!");
                        }
                    }
                }
            }).start();

            in.close();
            out.close();
            reader.close();
            socket.close();
            System.out.println("this client is disconnected from the server!!");
        }
    }
}

//----------------------------------------------------------------

//----------------------------------------------------------------
