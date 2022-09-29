import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
public class Chatter{
    String name;
    Socket socket;    
    DataInputStream in;
    DataOutputStream out;
    public Chatter(){
    }
    public Chatter(String name, Socket socket) throws IOException{
        this.name = name;
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public DataInputStream getIn() throws IOException {
        return in;
    }
    public void setIn(DataInputStream in) {
        this.in = in;
    }
    public DataOutputStream getOut() throws IOException {
        return out;
    }
    public void setOut(DataOutputStream out) {
        this.out = out;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    } 
}
