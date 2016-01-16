package core;

/**
 *
 * @author UlTMATE
 */
import java.io.*;
import java.net.*;

public class Client implements Serializable {
    
    private static final long serialVersionUID = 2123L;
    private int PORT;
    final String NAME;
    transient private Socket clientSocket;
    transient private ObjectOutputStream  outStream;
    
    public Client(String name){
        this.NAME = name;
    }
    
    public ObjectOutputStream getSocketOutputStream(){
        return outStream;
    }
    
    public void setSocketOutputStream(OutputStream stream) throws IOException{
        
        this.outStream = new ObjectOutputStream(stream);
    }
    
    public int getServerPortNumber(){
        return PORT;
    }
    
    public void setServerPortNumber(int port){
        this.PORT = port;
    }
    
    public Socket getSocket(){
        return clientSocket;
    }
    
    public void setSocket(Socket sock){
        clientSocket = sock;
    }
    
    public String getName(){
        return NAME;
    }
}
