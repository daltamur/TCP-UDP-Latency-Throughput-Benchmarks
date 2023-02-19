package ClientTypes;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;


public class TCPClient extends Client {
    public TCPClient(int Port, int ackPort, String hostIP) {
        super(Port, ackPort, hostIP);
    }

    @Override
    public void Connect() {

        try {
            InetAddress ip = InetAddress.getByName(hostIP);
            Socket serverConnection = new Socket(ip, Port);
            OutputStream out = serverConnection.getOutputStream();
            InputStream in = serverConnection.getInputStream();

            //Latency Testing
            System.out.println("\n---TCP Latency Values---");
            MeasureLatency(out, in , new byte[8]);
            MeasureLatency(out, in , new byte[32]);
            MeasureLatency(out, in , new byte[512]);
            MeasureLatency(out, in , new byte[1024]);

            //Throughput Testing
            System.out.println("---TCP Throughput Values---");
            MeasureThroughput(out, in, new byte[1024], 1024);
            MeasureThroughput(out, in, new byte[512], 2048);
            MeasureThroughput(out, in, new byte[128], 512);

            in.close();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

    }


    public void MeasureLatency(OutputStream out, InputStream in, byte[] msg) throws IOException {
        double totalTime = 0;
        //fill up the packet data with random values
        new Random().nextBytes(msg);
        for(int i = 0; i<1000; i++){
            byte[] ackMsg = new byte[msg.length];
            //start timer
            long curTime = System.nanoTime();
            //encrypt message
            out.write(Runner.XOREncryptDecrypt.EncryptDecrypt(msg));
            //read in the server's attempt to decrypt the message
            in.read(ackMsg);
            //check if the message was actually decrypted
            if(!new String(ackMsg).equals(new String(msg))){
                System.out.println("Error. Messages do not match");
            }
            //add the time this process took to the total time
            totalTime+=(System.nanoTime()-curTime)/1E9;
            out.flush();
        }
        //tell the server to stop taking in data
        out.write("STOP".getBytes());
        byte[] ackMsg = new byte[msg.length];
        in.read(ackMsg);
        //print out the average latency
        System.out.println(totalTime/1000.0 + " seconds for " + msg.length + " bytes");
        out.flush();

    }

    public void MeasureThroughput(OutputStream out, InputStream in, byte[] msg, int numOfMsgs) throws IOException {
        new Random().nextBytes(msg);
        byte[] decryptedMsg = new byte[8];
        byte[] initialMsgSubset = new byte[8];
        long startTime = System.nanoTime();
        for(int i = 0; i< numOfMsgs; i++){
            //acknowledgment message will always be 8 bytes
            out.write(Runner.XOREncryptDecrypt.EncryptDecrypt(msg));
            in.read(decryptedMsg);
            //check if the decrypted message is the same as the first 8 bytes of the original message
            System.arraycopy(msg, 0, initialMsgSubset, 0 ,8);
            if(!new String(decryptedMsg).equals(new String(initialMsgSubset))){
                System.out.println("Error: Messages do not match");
            }
            out.flush();
        }

        //calculate and print throughput
        double timeInSecs = ((double) (System.nanoTime() - startTime))/1E9;
        double throughput = (numOfMsgs * msg.length * 8)/timeInSecs;
        System.out.println(numOfMsgs + "X" + msg.length + "B: " + throughput + " b/s");

        //tell server to stop waiting for messages
        out.write("STOP".getBytes());
        //wait to get acknowledgement that server has stopped
        in.read(msg);
        out.flush();
    }

}
