import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Chinthan Bhat

 *
 * on 11/28/2016.
 */
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Integer.parseInt;

public class OFtpServer
{
    public static void main(String args[]) throws Exception {
        DataInputStream din;
        DataOutputStream dout;
        ServerSocket soc = new ServerSocket(21);
        ServerSocket customSoc = new ServerSocket(20);
        System.out.println("Waiting for Connection ...");
        System.out.println("FTP Server Started on Port Number 21");
//        while (true) {
            CommandThread C = new CommandThread(soc.accept());
            //break;
//        }
        while (true) {
            transferfile t = new transferfile(customSoc.accept());
        }
        //C.join();
        //t.join();

    }
}

class CommandThread extends Thread{

    DataInputStream din;
    DataOutputStream dout;
    Socket ClientSoc;


    CommandThread(Socket soc)
    {
        while(true) {


            try {
                ClientSoc = soc;
                din = new DataInputStream(ClientSoc.getInputStream());
                dout = new DataOutputStream(ClientSoc.getOutputStream());
                System.out.println("FTP Client Connected ...");
                start();
                break;
                //dout.writeUTF("Success");


            } catch (Exception ex) {
            }
        }

    }


    //System.out.println(comb);
    //String [] ret = comb.split(" ");
    //int portNo = parseInt();
    //String IP = ret[0];
    //System.out.println(IP + portNo);
//    public void run() {
//        String portNoI = null;
//        try {
//            portNoI = din.readUTF();
//            System.out.println(portNoI);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ServerSocket customSoc = null;
//        //int portNo = parseInt(portNoI);
//        try {
//            customSoc = new ServerSocket(20);
//            dout.writeUTF("Success");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Connecting to port 20 " );
//        while (true) {
//            System.out.println("Waiting for Connection ...");
//            try {
//                transferfile t = new transferfile(customSoc.accept());
//                break;
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}

class transferfile extends Thread
{

    Socket ClientSoc;
    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;

    transferfile(Socket soc)
    {
        try
        {
            ClientSoc=soc;
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            System.out.println("FTP Client Connected ...");
            start();

        }
        catch(Exception ex)
        {
        }
    }
    void SendFile() throws Exception
    {
        user();

        BufferedInputStream bis = null;
        OutputStream os = null;
        final ReentrantLock lock = new ReentrantLock();
        String filename="/home/ec2-user/FTP SERVER DIRECTORY/"+din.readUTF();
        //String filename="C:\\FTP SERVER DIRECTORY\\"+din.readUTF();


        System.out.println(filename);
        File f=new File(filename);
        FileInputStream fis = null;
        if(!f.exists())
        {
            //dout.writeUTF("File Not Found");
            return;
        }
        else {
//            dout.writeUTF("READY");
//            FileInputStream fin=new FileInputStream(f);
//            int ch;
//            do
//            {
//                ch=fin.read();
//                dout.writeUTF(String.valueOf(ch));
//            }
//            while(ch!=-1);
//            fin.close();
//            dout.writeUTF("File Receive Successfully");
            try {
                System.out.println("Found");
                //dout.writeUTF("Found");
                //File myFile = new File(filename);
                try {
                    lock.lock();
                    byte[] mybytearray = new byte[(int) f.length()];
                    fis = new FileInputStream(f);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray, 0, mybytearray.length);
                    os = ClientSoc.getOutputStream();
                    System.out.println("Sending " + filename + "(" + mybytearray.length + " bytes)");
                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();
                    System.out.println("Sent to Client.");
                    //dout.writeUTF("Sent to client");
                    fis.close();
                    return;
                }
                catch(IOException e){
                    System.out.println("Test");
                    e.printStackTrace();
                }

                finally{
                    lock.unlock();
                }


            } finally {
                if (bis != null) bis.close();
                if (os != null) os.close();
                //if (ClientSoc != null) ClientSoc.close();
                //run();
                //return;
            }
        }

    }

    void ReceiveFile() throws Exception
    {
        String fileName;
        user();
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        String filename = din.readUTF();
        String path = "/home/ec2-user/FTP SERVER DIRECTORY/"+filename;
        //String path = "C:\\FTP SERVER DIRECTORY\\"+filename;


        System.out.println(path);
        //System.out.print("Enter File Name :");
        //fileName=br.readLine();
        //dout.writeUTF(fileName);
        //String msgFromServer=din.readUTF();
        //System.out.println(msgFromServer);
        //receive file
        File FILE_TO_RECEIVED = new File(path);

        if (FILE_TO_RECEIVED.createNewFile()){
            System.out.println("File is created!");
        }else{
            System.out.println("File already exists.");
            //return;
        }
        FileOutputStream s = new FileOutputStream(FILE_TO_RECEIVED,false);
        byte [] mybytearray  = new byte [69223860];
        InputStream is = ClientSoc.getInputStream();
        fos = new FileOutputStream(FILE_TO_RECEIVED);
        bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray,0,mybytearray.length);
        current = bytesRead;

        do {
            bytesRead =
                    is.read(mybytearray, current, (mybytearray.length-current));
            if(bytesRead >= 0) current += bytesRead;
        } while(bytesRead > -1);

        bos.write(mybytearray, 0 , current);
        bos.flush();
        System.out.println("File " + FILE_TO_RECEIVED
                + " downloaded (" + current + " bytes read)");
        fos.close();
        return;
    }
    void connectPort() throws Exception
    {
        //System.out.println("Test");
        String portNoI = din.readUTF();
        //System.out.println(comb);
        //String [] ret = comb.split(" ");
        int portNo;
        portNo = parseInt(portNoI);
        //String IP = ret[0];
        //System.out.println(IP + portNo);
        ServerSocket customSoc = new ServerSocket(portNo);

        System.out.println("Connecting to port " + portNo);
        while(true) {
            System.out.println("Waiting for Connection ...");
            transferfile t = new transferfile(customSoc.accept());
        }
    }
    void user() throws Exception{
        String user=din.readUTF();
        int flag;
        br = new BufferedReader(new FileReader("abc.txt"));
        String s = "", line = null;
        while ((line = br.readLine()) != null) {
            s += line + "\n";
        }
        if (s.contains(user)) {
            flag = 1;
        } else {
            flag = 0;
        }

        if (flag == 1) {
            System.out.print("Success");
            dout.writeUTF("Success");
        } else{
            System.out.print("Failed");
            dout.writeUTF("Failed");
            //System.exit(1);
        }
        //dout.writeUTF("Failed");
    }

    void register() throws Exception
    {

        FileWriter fw = new FileWriter("abc.txt",true);
        String user=din.readUTF();

        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("\n"+user+"\n");
        bw.close();
    }


    public void run()
    {
//         while(true)
//        {
        try
        {
            System.out.println("Waiting for Command ...");
            //dout.writeUTF("Waiting");
            String Command=din.readUTF();
            //System.out.println("2");
            if(Command.compareTo("GET")==0)
            {
                System.out.println("\tGET Command Received ...");
                SendFile();
                //continue;
            }
            else if(Command.compareTo("SEND")==0)
            {
                System.out.println("\tSEND Command Receiced ...");
                ReceiveFile();
                //continue;
            }
            else if(Command.compareTo("PortCmd")==0){
                System.out.println("\t PORT command recieved...");
                connectPort();
                //continue;
            }
            else if(Command.compareTo("DISCONNECT")==0)
            {
                System.out.println("\tDisconnect Command Received ...");
                System.exit(1);
            }
            else if(Command.compareTo("REGISTER")==0)
            {

                System.out.println("\tRegister command recieved...");
                register();
            }
            else if(Command.compareTo("NOOP")==0)
            {

                System.out.println("\tNOOP command recieved...");
                dout.writeUTF("Server is active");
            }
        }
        catch(Exception ex)
        {
        }

//        }
    }
}
