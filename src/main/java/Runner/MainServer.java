package Runner;

import ServerTypes.TCPServer;
import ServerTypes.UDPServer;
import java.io.IOException;

public class MainServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        UDPServer UDPServer = new UDPServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        UDPServer.Serve();

        TCPServer TCPServer = new TCPServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        TCPServer.Serve();

    }

}
