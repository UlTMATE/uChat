package core;

/**
 *
 * @author UlTMATE
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class Friend implements ActionListener, Serializable{
    
    private final String NAME;
    private final int PORT;
    transient public JTextArea chatTA;
    JTextField messageTf;
    transient JFrame friendFrame;
    
    
    public Friend(String name, int port){
        this.NAME=name;
        this.PORT=port;
    }
    
    public String getName(){
        return NAME;
    }
    
    public int getServerPortNumber(){
        return PORT;
    }
    
    public void showFriendScreen(){
        friendFrame = new JFrame("Chat With: " +NAME);
        friendFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        friendFrame.setSize(300,300);
        friendFrame.setLocationRelativeTo(Home.homeFrame);
        friendFrame.setBackground(Color.ORANGE);
        JPanel centPan = new JPanel(new BorderLayout());
        centPan.setOpaque(false);
        centPan.setBorder(BorderFactory.createEmptyBorder(2,2,1,2));
        chatTA = new JTextArea("");
        chatTA.setEditable(false);
        chatTA.setBackground(new Color(216,239,232));
        chatTA.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        centPan.add(new JScrollPane(chatTA), "Center");
        
        JPanel botPan = new JPanel(new BorderLayout());
        botPan.setOpaque(false);
//        botPan.setBackground(Color.ORANGE);
        botPan.setBorder(BorderFactory.createEmptyBorder(2,2,1,2));
        messageTf = new JTextField("");
        messageTf.addActionListener(this);
        messageTf.setCaretColor(Color.blue);
        botPan.add(messageTf);
        
        friendFrame.add(centPan,"Center");
        friendFrame.add(botPan, "South");
        friendFrame.setVisible(true);
        messageTf.requestFocus();
    }
    
    public boolean isActive(){
        if(friendFrame==null){
            return false;
        }
        return true;
    }
    
    @Override
    public void actionPerformed(ActionEvent axnEve){
        Object obj = axnEve.getSource();
        if(obj==messageTf){
            if(!messageTf.getText().equals("")) {
                try {
                    InetAddress adrs = InetAddress.getByName("localhost");
                    
//                        ChatScreen.objWriter.writeObject(Home.client);
                        ChatScreen.objWriter.writeObject(this);
                        ChatScreen.objWriter.writeObject(new String(messageTf.getText()));
                        System.out.println("Message Sent " +messageTf.getText());
                        chatTA.append("\nYou: " + messageTf.getText() + "");
                        chatTA.setCaretPosition(chatTA.getText().length());
                    
                } catch (UnknownHostException unhoExc) {
                    JOptionPane.showMessageDialog(null, "Server IP is not valid. Restart App");
                } catch(IOException ioExc){
                    friendFrame.setVisible(false);
                    friendFrame.dispose();
                    JOptionPane.showMessageDialog(friendFrame, "Your Friend Went Offline");
                }
                messageTf.setText("");
            }
        }
    }
}
