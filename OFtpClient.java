import java.io.*;
import java.net.Socket;

import static com.sun.javafx.util.Utils.split;
import static java.lang.Thread.sleep;

/**
 * Created by Chinthan Bhat

 *
 *
 * on 11/28/2016.
 */
import java.net.*;
import java.io.*;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


class OFtpClient
{
    public static void main(String args[]) throws Exception
    {


        Socket soc=new Socket("ec2-54-71-20-30.us-west-2.compute.amazonaws.com",21);
        //Socket soc=new Socket("127.0.0.1",21);
        CommandChannel C=new CommandChannel(soc);
        Socket customSoc=new Socket("ec2-54-71-20-30.us-west-2.compute.amazonaws.com",20);
//        Socket customSoc = new Socket("127.0.0.1",20);
        transferfileClient t = new transferfileClient(customSoc);
        t.displayMenu();

    }
}

class CommandChannel {


//    CommandChannel(){
//        System.out.println("Command Channel is active");
//    }

    Socket ClientSoc;

    DataInputStream din2;
    DataOutputStream dout2;
    BufferedReader br2;
    String response;
    //Shared variables:
    //int sh1=1,sh2 =1;
    CommandChannel(Socket soc) {
        while (true) {
            try {
                ClientSoc = soc;
                din2 = new DataInputStream(ClientSoc.getInputStream());
                dout2 = new DataOutputStream(ClientSoc.getOutputStream());
                br2 = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Command Channel is active");
                break;


            }catch (Exception ex) {
           }
        }
    }
//                dout2.writeUTF("20");
//                sleep(1000);
//                response = din2.readUTF();
//                System.out.println(response);
////            DataChannel(response);
//                System.out.println("Sending through command channel to open data channel");
//                String response2 = response.toString();
//                boolean i = response2.equalsIgnoreCase("Success");
//                System.out.println(i);
//                if (response.equalsIgnoreCase("Success")) {
//
//                    //Socket customSoc=new Socket("ec2-54-71-20-30.us-west-2.compute.amazonaws.com",20);
//                    Socket customSoc = new Socket("127.0.0.1", 20);
//                    System.out.println("TestDC");
//
//                    transferfileClient t = new transferfileClient(customSoc);
//                    t.displayMenu();
//
//
//                }
//            } catch (Exception ex) {
//            }
//        }
//    }

//
//    public void DataChannel(String response) throws Exception{
//
//        if( response == "Success"){
//            Socket soc=new Socket("127.0.0.1",20);
//            System.out.println("TestDC");
//
//            transferfileClient t=new transferfileClient(soc);
//            t.displayMenu();
//
//
//        }
//    }

}
class transferfileClient
{
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;
    //Shared variables:
    //int sh1=1,sh2 =1;
    transferfileClient(Socket soc)
    {
        try
        {
            ClientSoc=soc;
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            br=new BufferedReader(new InputStreamReader(System.in));
        }
        catch(Exception ex)
        {
        }
    }
    void SendFile() throws Exception
    {
        user();
        System.out.println("Enter the file name");
        String fileTSend = br.readLine();
        final ReentrantLock lock = new ReentrantLock();
        String filename = "C:\\FTP CLIENT DIRECTORY\\"+fileTSend;

            BufferedInputStream bis = null;
            OutputStream os = null;

            dout.writeUTF(fileTSend);
            System.out.println(filename);
            File f = new File(filename);
            FileInputStream fis = null;
            if (!f.exists()) {
                System.out.println("File Not Found");
                return;
            } else {
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
                    byte[] mybytearray = new byte[(int) f.length()];
                    fis = new FileInputStream(f);
//                    FileLock lock = null;
//                    try {
//                            lock = fis.getChannel().lock();
//                    }
//                    catch(OverlappingFileLockException e){
//                        wait();
//                    }
                    try {
                        lock.lock();
                        // do the writing
                        bis = new BufferedInputStream(fis);
                        bis.read(mybytearray, 0, mybytearray.length);
                        os = ClientSoc.getOutputStream();
                        System.out.println("Sending " + filename + "(" + mybytearray.length + " bytes)");
                        os.write(mybytearray, 0, mybytearray.length);
                        os.flush();
                        //lock.release();
                        System.out.println("Sent to Server.");
                        fis.close();

                    } finally {
                        lock.unlock();
                        //notify();
                    }


                } finally {
                    if (bis != null) bis.close();
                    if (os != null) os.close();
                    //if (ClientSoc != null) ClientSoc.close();
                    //run();
                    return;

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
        System.out.print("Enter File Name :");
        fileName=br.readLine();
        dout.writeUTF(fileName);
//      String msgFromServer=din.readUTF();
//      System.out.println(msgFromServer);
        String path = "C:\\FTP CLIENT DIRECTORY\\"+fileName;
        System.out.println(path);
        //receive file
        File FILE_TO_RECEIVED = new File(path);

        if (FILE_TO_RECEIVED.createNewFile()){

            System.out.println("File is created!");

        }else{
            //System.out.println("File already exists.");
            String Option;
            System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
            Option=br.readLine();
            if(Option=="N")
            {
                //dout.flush();
                return;
            }
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

    void user() throws Exception {
        BufferedReader br = null;
        int flag = 0;

//        br = new BufferedReader(new FileReader("C:/Users/Paushali/IdeaProjects/OFTP/abc.txt"));
//        String s = "", line = null;
//        while ((line = br.readLine()) != null) {
//            s += line + "\n";
//        }
        System.out.println("Enter User Name: ");
        Scanner u = new Scanner(System.in);
        String user = u.nextLine();
        dout.writeUTF(user);
        String status= din.readUTF();
        if(status.equals("Success")){
            System.out.println("Logged in");
        }
        else{
            System.out.println("Wrong id");
            System.exit(1);
        }
//        if (s.contains(user)) {
//            flag = 1;
//        } else {
//            flag = 0;
//        }
//
//        if (flag == 1) {
//            //dout.writeUTF("Success");
//        } else{}
//            //dout.writeUTF("Failed");

    }

    void register() throws Exception
    {
        System.out.println("Enter New User Name: ");
        Scanner u = new Scanner(System.in);
        String user = u.nextLine();
        dout.writeUTF(user);

    }

    void connectPort () throws Exception {
        System.out.println("Enter the port No.:");
        String portNo = br.readLine();
        System.out.println("Sending port number to Server..");
        dout.writeUTF(portNo);
    }

    public void displayMenu() throws Exception
    {
        while(true)
        {

            System.out.println("[ MENU ]");
            System.out.println("1. STOR");
            System.out.println("2. RETR");
            //System.out.println("3. Login");
            System.out.println("3. PORT");
            System.out.println("4. REGISTER");
            System.out.println("5. NOOP");
            System.out.println("6. TYPE");
            System.out.println("7. MODE");
            System.out.println("8. EXIT");

            System.out.print("\nEnter Choice :");
            int choice;
            choice=Integer.parseInt(br.readLine());


            if(choice==1)
            {
                dout.writeUTF("SEND");
                SendFile();
                continue;
            }
            else if(choice==2)
            {
                dout.writeUTF("GET");
                ReceiveFile();
                continue;
            }
//            else if(choice==3)
//            {
//                user();
//                continue;
//            }
            else if(choice == 3)
            {
                dout.writeUTF("PortCmd");
                connectPort();
                continue;
            }
            else if(choice == 4)
            {
                dout.writeUTF("REGISTER");
                register();
                continue;
            }
            else if(choice == 5)
            {
                dout.writeUTF("NOOP");
                String responseNoop = din.readUTF();
                System.out.println(responseNoop);
                continue;
            }
            else if(choice == 6)
            {
                System.out.println("The TYPE is ASCII");
                continue;
            }
            else if(choice == 7)
            {
                System.out.println("The MODE is STREAM mode");
            }
            else
            {
                dout.writeUTF("DISCONNECT");
                System.exit(1);
            }
        }
    }
}
