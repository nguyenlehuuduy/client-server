import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
public class Client extends Thread{
    static JTextArea ta;
    JTextField txt;
    JButton b1, b2;
    String name = "";
    JFrame fr;
    Font myFont = new Font("Courier New", Font.PLAIN, 16);
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    static DefaultListModel listModel;
    static JList list;
    public Client(){
        String ip = "localhost";
        try {
            socket = new Socket(ip, 1234);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e){
            System.err.println("Client exception: " + e.toString());
        }
        fr = new JFrame("");
        fr.setLayout(new BorderLayout());
        fr.setFont(myFont);
        ta = new JTextArea("Welcome!\nEnter your nick and click Start");
        ta.setFont(myFont);
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);  
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(sp, BorderLayout.CENTER);
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        txt = new JTextField();
        txt.setFont(myFont);
        txt.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(!name.equals(""))
                        b1.doClick();
                    else
                        b2.doClick();
                }
            }
        });
        p1.add(txt, BorderLayout.CENTER);
        b1 = new JButton("Send");
        b1.addActionListener(new MyAction());
        b1.setEnabled(false);
        b2 = new JButton("Start");
        b2.addActionListener(new MyAction());
        p1.add(b2, BorderLayout.WEST);
        p.add(p1, BorderLayout.SOUTH);
        fr.add(p, BorderLayout.CENTER);
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.add(new JLabel("Chatters"), BorderLayout.NORTH);
        listModel = new DefaultListModel<String>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(list);
        listModel.addElement("(null)                         ");
        p2.add(listScrollPane, BorderLayout.CENTER);
        p2.add(b1, BorderLayout.SOUTH);
        fr.add(p2, BorderLayout.EAST);   
        fr.setSize(450,350);
        fr.setLocationRelativeTo(null);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                try {
                    out.writeUTF("Cancel");
                    socket.close();
                    System.exit(0);
                } catch (IOException ex) {
                    System.err.println("Client exception: " + ex.toString());
                }
            }
        });
        fr.setVisible(true);
    }
    @Override
    public void run() {
        while (true) {  
	    try{
		if (socket != null) {
                    String s1 = in.readUTF();
                    switch (s1) {
                        case "add":                               
                            String s = in.readUTF();
                            ta.append("\n[" + s + "] got connected");                            
                            listModel.addElement(s);
                            break;
                        case "insert":
                            int n = in.readInt();
                            if(n > 0){                        
                                for (int i = 0; i < n; i++)
                                    listModel.addElement(in.readUTF());
                            }
                            break;    
                        case "remove":
                            String s2 = in.readUTF();
                            listModel.removeElement(s2);
                            ta.append("\n[" + s2 +"] is leaving");
                            break;
                        case "Send":
                            ta.append("\n" + in.readUTF());
                            break;
                        case "SendPM":
                            ta.append("\n" + in.readUTF());
                            break;
                    }
                }
	    } catch (IOException e) {
		//Không viết vô đây
	    }
	}
    }
    class MyAction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String s = e.getActionCommand();
                String s1 = txt.getText();
                switch (s) {
                    case "Start":
                        name = s1;
                        boolean ok = true;
                        for (int i = 0; i < listModel.size(); i++)
                            if(name.equals(listModel.getElementAt(i))){
                                JOptionPane.showMessageDialog(fr, "The nick is in use", "Error",JOptionPane.ERROR_MESSAGE);
                                ok = false;
                                name = "";
                                break;
                            }
                        if(ok){
                            b2.setEnabled(false);
                            b1.setEnabled(true);
                            out.writeUTF(s1);
                            txt.setText("");
                            fr.setTitle(name);
                            b1.setText("Send");
                        }
                        break;
                    case "Send":
                        int i = list.getSelectedIndex() - 1;
                        if(i < 0){
                            txt.setText("");
                            out.writeUTF("Send");
                            out.writeUTF(s1);
                            out.writeUTF(name);
                        }
                        else{
                            out.writeUTF("SendPM");
                            ta.append("\n[PM to " + list.getSelectedValue() + "] " + s1);
                            txt.setText("");
                            out.writeUTF(s1);
                            out.writeInt(i);
                            out.writeUTF(name);
                        }
                }
            } catch (IOException ex) {
                System.err.println("Client exception: " + ex.toString());
            }
        }        
    }    
    public static void main(String[] args) {
        Client c = new Client();
        c.start();
    }
}