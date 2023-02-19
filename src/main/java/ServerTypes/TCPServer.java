package ServerTypes;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Server {
    public TCPServer(int Port, int ackPort) {
        super(Port, ackPort);
    }

    @Override
    public void Serve(){
        try {
            ServerSocket serverSocket = new ServerSocket(Port);
            Socket clientConnection = serverSocket.accept();
            InputStream in = clientConnection.getInputStream();
            OutputStream out = clientConnection.getOutputStream();
            receiveAndAck(in, out, new byte[8]);

            receiveAndAck(in, out, new byte[32]);

            receiveAndAck(in, out, new byte[512]);

            receiveAndAck(in, out, new byte[1024]);

            receiveAndAckEightBytes(in, out, new byte[1024]);

            receiveAndAckEightBytes(in, out, new byte[512]);

            receiveAndAckEightBytes(in, out, new byte[128]);

            in.close();
            out.close();
        } catch (Exception e) {e.printStackTrace();}
    }


    public void receiveAndAck(InputStream in, OutputStream out, byte[] receivedMsg) throws IOException {
        //do this loop forever until a message saying to stop is received
        for(;;){
            //take in the data
            in.read(receivedMsg);
            if(new String(receivedMsg).contains("STOP")){
                break;
            }
            //decrypt the data
            out.write(Runner.XOREncryptDecrypt.EncryptDecrypt(receivedMsg));
            //send back decrypted message
            receivedMsg = new byte[receivedMsg.length];
            out.flush();
        }
        //tell the client that no more data will be read on this run
        out.write("OK".getBytes());
        out.flush();
    }


    public void receiveAndAckEightBytes(InputStream in, OutputStream out, byte[] receivedMsg) throws IOException {
        //do this loop until a message saying to stop is received
        for(;;) {
            byte[] ack = new byte[8];
            //read in the data
            in.read(receivedMsg);
            if(new String(receivedMsg).contains("STOP"))
                break;
            //decrypt the data and send back the first 8 bytes of the decrypted message
            receivedMsg = Runner.XOREncryptDecrypt.EncryptDecrypt(receivedMsg);
            //get the first 8 bytes of the decrypted message and write it back to the sender for confirmation that the message was properly received
            System.arraycopy(receivedMsg, 0, ack, 0, 8);
            out.write(ack);
            out.flush();
        }

        //send message letting client know that the stop message was received
        out.write("OK".getBytes());
        out.flush();
    }
}
