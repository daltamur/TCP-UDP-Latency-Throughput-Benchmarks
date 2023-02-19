package Runner;

import ClientTypes.TCPClient;
import ClientTypes.UDPClient;

import java.io.IOException;

public class MainClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        UDPClient UDPClient = new UDPClient(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]);
        UDPClient.Connect();

        Thread.sleep(5000);
        TCPClient TCPClient = new TCPClient(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]);
        TCPClient.Connect();
    }

}
