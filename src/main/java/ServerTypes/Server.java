package ServerTypes;

import java.io.IOException;

public abstract class Server {
    final Integer Port;
    final Integer ackPort;

    public Server(int Port, int ackPort){
        this.Port = Port;
        this.ackPort = ackPort;
    }

    public abstract void Serve() throws IOException, InterruptedException;

}
