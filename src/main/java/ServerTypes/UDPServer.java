package ServerTypes;

import Runner.XOREncryptDecrypt;

import java.io.IOException;
import java.net.*;

public class UDPServer extends Server {
    public UDPServer(int Port, int ackPort) throws UnknownHostException {
        super(Port, ackPort);
    }

    @Override
    public void Serve() throws IOException, InterruptedException {
        ReceiveAndAck(new DatagramSocket(Port), new DatagramPacket(new byte[8], 8));

        ReceiveAndAck(new DatagramSocket(Port), new DatagramPacket(new byte[32], 32));

        ReceiveAndAck(new DatagramSocket(Port), new DatagramPacket(new byte[512], 512));

        ReceiveAndAck(new DatagramSocket(Port), new DatagramPacket(new byte[1024], 1024));

        ReceiveAndAckEightBytes(new DatagramSocket(Port), new DatagramPacket(new byte[1024], 1024));

        ReceiveAndAckEightBytes(new DatagramSocket(Port), new DatagramPacket(new byte[512], 512));

        ReceiveAndAckEightBytes(new DatagramSocket(Port), new DatagramPacket(new byte[128], 128));
    }

    public void ReceiveAndAck(DatagramSocket clientSocket, DatagramPacket packet) throws IOException {
        //do this loop until a message is received saying to stop taking in data
        for (;;){
            //read in data
            clientSocket.receive(packet);
            if(new String(packet.getData()).contains("STOP"))
                break;
            //decrypt data
            DatagramPacket ackPacket = new DatagramPacket(XOREncryptDecrypt.EncryptDecrypt(packet.getData()), packet.getLength(), packet.getAddress(), ackPort);
            //send back devrypted data
            clientSocket.send(ackPacket);
        }
        //tell the client that you have stopped accepting data for this current iteration
        System.out.println("Stopped sending.");
        clientSocket.send(new DatagramPacket("OK".getBytes(), "OK".getBytes().length, packet.getAddress(), ackPort));
        clientSocket.close();
    }

    public void ReceiveAndAckEightBytes(DatagramSocket clientSocket, DatagramPacket packet) throws IOException {
        //do this loop until a message is received saying to stop taking in data
        for (;;){
            byte[] ack = new byte[8];
            //take in data
            clientSocket.receive(packet);
            if(new String(packet.getData()).contains("STOP"))
                break;
            //decrypt the data
            System.arraycopy(XOREncryptDecrypt.EncryptDecrypt(packet.getData()), 0, ack, 0, 8);
            DatagramPacket ackPacket = new DatagramPacket(ack, ack.length, packet.getAddress(), ackPort);
            //send back first 8 bytes of the decrypted data
            clientSocket.send(ackPacket);
        }
        //tell the client that you have stopped accepting data for this current iteration
        System.out.println("Stopped sending.");
        clientSocket.send(new DatagramPacket("OK".getBytes(), "OK".getBytes().length, packet.getAddress(), ackPort));
        clientSocket.close();
    }
}