import java.io.*;
public class RequestProcessing extends Thread {
    Chatter chatter; 
    public RequestProcessing(Chatter chatter){
        this.chatter = chatter;
    }
    @Override
    public void run(){
        try{
            String action;
            int i;
            while(true){
                action = chatter.getIn().readUTF();
                if(action.equals("Cancel")){
                    chatter.getSocket().close();
                    Server.list.remove(chatter);
                    for (Chatter client : Server.list){
                        client.getOut().writeUTF("remove");
                        client.getOut().writeUTF(chatter.getName());
                    }
                    break;
                }
                switch(action){
                    case "Send":
                        String msg = chatter.in.readUTF();
                        String name = chatter.in.readUTF();
                        for (Chatter client : Server.list) {
                            client.getOut().writeUTF("Send");
                            client.getOut().writeUTF("[" + name + "] " + msg);
                        }
                        break;
                    case "SendPM":
                        String msg1 = chatter.in.readUTF();
                        int j = chatter.in.readInt();
                        String name1 = chatter.in.readUTF();
                        DataOutputStream out = Server.list.get(j).getOut();
                        out.writeUTF("SendPM");
                        out.writeUTF("[PM from " + name1 + "] "+ msg1);                        
                        break;
                }
            }
        }catch(IOException e){
            System.out.println("Error: " + e);
        }
        finally{
            if(chatter != null)
                try {
                    chatter.getSocket().close();
                    Server.list.remove(chatter);
                } catch (IOException ex) { }
        }
    }
}
