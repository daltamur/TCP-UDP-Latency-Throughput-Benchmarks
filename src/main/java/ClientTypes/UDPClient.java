package ClientTypes;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Random;

public class UDPClient extends Client {
    private final InetAddress ip;

    public UDPClient(int Port, int ackPort, String hostIP) throws UnknownHostException {
        super(Port, ackPort, hostIP);
        this.ip = InetAddress.getByName(hostIP);
    }

    @Override
    public void Connect() {

        try {
            System.out.println("---UDP Latency Values---");
            //now that a connection has been established, start the RTT tests
            MeasureLatency(new DatagramSocket(ackPort), new byte[8]);
            MeasureLatency(new DatagramSocket(ackPort), new byte[32]);
            MeasureLatency(new DatagramSocket(ackPort), new byte[512]);
            MeasureLatency(new DatagramSocket(ackPort), new byte[1024]);

            //Throughput Testing
            System.out.println("---UDP Throughput Values---");
            MeasureThroughput(new DatagramSocket(ackPort), new byte[1024], 1024);
            MeasureThroughput(new DatagramSocket(ackPort), new byte[512], 2048);
            MeasureThroughput(new DatagramSocket(ackPort), new byte[128], 8192);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public void MeasureLatency(DatagramSocket serverSocket, byte[] msg) throws IOException {
        double totalTime = 0;
        DatagramPacket packet;
        DatagramPacket receivedPacket;
        //fill up packet data with random values
        new Random().nextBytes(msg);

        for(int i = 0; i<1000; i++){
            receivedPacket = new DatagramPacket(new byte[msg.length], msg.length);

            //begin timer
            long curTime = System.nanoTime();

            //encrypt data
            serverSocket.send(new DatagramPacket(Runner.XOREncryptDecrypt.EncryptDecrypt(msg), msg.length, ip, Port));
            //receive the server's attempt to decrypt the data
            serverSocket.receive(receivedPacket);
            if(!new String(msg).equals(new String(receivedPacket.getData(), 0 , receivedPacket.getLength())))
                System.out.println("Error: Messages Do Not Match.");

            //add the total time this process took to the total time
            totalTime+=(System.nanoTime()-curTime)/1E9;
        }

        //tell server to stop taking data
        packet = new DatagramPacket("STOP".getBytes(), "STOP".getBytes().length, ip, Port);
        serverSocket.send(packet);
        //get the ack from the server that tells us it has stopped receiving data
        receivedPacket = new DatagramPacket(new byte[msg.length], msg.length);
        serverSocket.receive(receivedPacket);

        System.out.println(totalTime/1000.0 + " seconds for " + msg.length + " bytes");
        serverSocket.close();
    }

    public void MeasureThroughput(DatagramSocket serverSocket, byte[] msg, int numOfMsgs) throws IOException {
        //fill up packet data with random bytes
        new Random().nextBytes(msg);
        DatagramPacket receivedPacket = new DatagramPacket(new byte[8], 8);
        DatagramPacket packet;
        byte[] receivedMsgSubset = new byte[8];

        //get start time
        long startTime = System.nanoTime();
        for(int i = 0; i<numOfMsgs; i++){
            //encrypt message
            serverSocket.send(new DatagramPacket(Runner.XOREncryptDecrypt.EncryptDecrypt(msg), msg.length, ip, Port));
            //receive the server's 8 byte acknowledgement
            serverSocket.receive(receivedPacket);
            System.arraycopy(receivedPacket.getData(), 0, receivedMsgSubset, 0, 8);
            //see if the decrypted message matches up with the first 8 bits of the original message
            if(!new String(receivedMsgSubset).equals(new String(receivedPacket.getData())))
                System.out.println("Error: Messages Do Not Match.");
        }

        //calculate and print throughput
        double timeInSecs = ((double) (System.nanoTime() - startTime))/1E9;
        double throughput = (numOfMsgs * msg.length * 8)/timeInSecs;
        System.out.println(numOfMsgs + "X" + msg.length + "B: " + throughput + " b/s");

        //Tell the server to stop accepting messages
        packet = new DatagramPacket("STOP".getBytes(), "STOP".getBytes().length, ip, Port);
        serverSocket.send(packet);
        receivedPacket = new DatagramPacket(new byte[msg.length], msg.length);
        serverSocket.receive(receivedPacket);
        serverSocket.close();
    }

}
