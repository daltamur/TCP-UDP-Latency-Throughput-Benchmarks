package ClientTypes;

public abstract class Client {

    final Integer Port;
    final Integer ackPort;

    final String hostIP;

    public Client(int Port, int ackPort, String hostIP){
        this.Port = Port;
        this.ackPort = ackPort;
        this.hostIP = hostIP;
    }

    public abstract void Connect();

}
