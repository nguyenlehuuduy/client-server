import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class Server {
    static ArrayList<Chatter> list;
    public static void main(String args[]){ 
        try {
            list = new ArrayList<>();
            ServerSocket ss = new ServerSocket(1234);
            System.out.println("[System] Server is ready...");
            while (true) {
                try{                        
                    Socket s = ss.accept();
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeUTF("insert");
                    int n = list.size();
                    out.writeInt(n);
                    for (int i = 0; i < n; i++){
                        out.writeUTF(list.get(i).getName());
                    }
                    String name = in.readUTF();
                    if(!name.equals("Cancel")){
                        Chatter chatter = new Chatter(name, s);
                        list.add(chatter);
                        for (Chatter c: list){
                            DataOutputStream out1 = c.getOut();
                            out1.writeUTF("add");
                            out1.writeUTF(name);
                        }                 
                        RequestProcessing rp = new RequestProcessing(chatter);
                        rp.start();
                    }
                }catch (IOException e){
                    System.err.println("Server exception: " + e);
                }
            }
        }catch (IOException e) {
            System.err.println("Server exception: " + e);
        }
    }
}