package core;

/**
 *
 * @author UlTMATE
 */
import static core.Home.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class ChatScreen extends MouseAdapter implements ActionListener {

    public static JPanel clientsPan;
    JSplitPane splitPane;
    public static JTextArea globalTA;
    JTextField messageTf;
    public static DefaultListModel listModel;
    public static JList clientsList;
    public static ArrayList friends;
    static ObjectOutputStream objWriter;
    ObjectInputStream objReader;

    public ChatScreen() {
        try {
            
            InetAddress adrs = InetAddress.getByName(serverIp);
            Socket sock = new Socket(adrs, 8085);

//            System.out.println("accept");
            objWriter = new ObjectOutputStream(sock.getOutputStream());
//                    System.out.println((objWriter==null)+ " "+ (objReader==null));
            objWriter.writeObject(Home.client);
            new Thread(() -> {
                try {

                    objReader = new ObjectInputStream(sock.getInputStream());

                    while (true) {
                        Object obj = objReader.readObject();
                        System.out.println("Reading Object");
                        if (obj instanceof String) {
                            System.out.println("A global text received -> " + obj.toString());
                            ChatScreen.globalTA.append("\n\n" + obj.toString());
                            ChatScreen.globalTA.setCaretPosition(ChatScreen.globalTA.getText().length());
//                            System.out.println("Written String");
                            Home.homeFrame.setVisible(true);
                        } else if (obj instanceof Client) {
                            Client receivedFriend = (Client) obj;
                            System.out.println("A private text received from -> " + receivedFriend.getName());
                            int index = -1;
                            ListIterator liter = ChatScreen.friends.listIterator();
                            while (liter.hasNext()) {
                                Friend temp = (Friend) liter.next();
//                                System.out.println(receivedFriend.getName() + " " + temp.getName());
//                                System.out.println(receivedFriend.getServerPortNumber() + " " + temp.getServerPortNumber());
                                if ((receivedFriend.getName().equals(temp.getName())) && receivedFriend.getServerPortNumber() == temp.getServerPortNumber()) {
                                    index = ChatScreen.friends.indexOf(temp);
                                    break;
                                }
                            }
                            System.out.println(index);
                            if (index != -1) {
                                String msg = (String) objReader.readObject();
                                Friend friend = (Friend) ChatScreen.friends.get(index);
                                if (!friend.isActive()) {
                                    friend.showFriendScreen();
                                }
                                friend.chatTA.append("\n\n" + friend.getName() + ": " + msg);
                                friend.chatTA.setCaretPosition(friend.chatTA.getText().length());
                                friend.friendFrame.setVisible(true);
                            }
                        } else if (obj instanceof ArrayList) {
                            ChatScreen.friends = new ArrayList();
                            ArrayList list = (ArrayList) obj;
                            ListIterator iter = list.listIterator();
                            while (iter.hasNext()) {
                                Client client = (Client) iter.next();
                                ChatScreen.friends.add(new Friend(client.getName(), client.getServerPortNumber()));
                            }
                            if (Home.chScr != null) {
                                Home.chScr.setClientsPan();
                            }
                        }
                    }
                } catch (IOException ioExc) {
                    ioExc.printStackTrace();
                    JOptionPane.showMessageDialog(Home.homeFrame, "Server is offline");
                } catch (ClassNotFoundException cnfExc) {
                    cnfExc.printStackTrace();
                }
            }).start();
            showChatScreen();
        } catch (UnknownHostException unhoExc) {
            JOptionPane.showMessageDialog(homeFrame, "Invalid Server IP", "Check IP", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            JOptionPane.showMessageDialog(homeFrame, "No server found at requested IP", "Check IP", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showChatScreen() {
        JLabel titleLab = new JLabel("Welcome: " + client.getName());
        titleLab.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        titleLab.setForeground(Color.yellow);
        JPanel titlePan = new JPanel();
        titlePan.setBackground(Color.darkGray);
        titlePan.add(titleLab);

        JPanel globalChatPan = new JPanel(new BorderLayout());
        globalTA = new JTextArea("");
        globalTA.setEditable(false);
        globalTA.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        globalTA.setLineWrap(true);

        JPanel botPan = new JPanel(new BorderLayout());
        botPan.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));
        messageTf = new JTextField("");
        messageTf.addActionListener(this);
        messageTf.setCaretColor(Color.red);
        messageTf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        messageTf.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.darkGray));
        botPan.add(messageTf, "Center");
        JScrollPane jsp = new JScrollPane(globalTA);
        jsp.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.darkGray));
        globalChatPan.add(jsp, "Center");
        globalChatPan.add(botPan, "South");

        clientsPan = new JPanel();
        clientsPan.setBackground(Color.darkGray);
        listModel = new DefaultListModel();
        clientsList = new JList(listModel);
        clientsList.addMouseListener(this);
        clientsList.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clientsList.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        clientsList.setOpaque(true);
        clientsList.setBackground(Color.gray);
        clientsList.setForeground(Color.white);
        clientsPan.add(clientsList);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, clientsPan, globalChatPan);

        homeFrame.remove(centerPan);
        homeFrame.remove(topPan);
        centerPan = new JPanel(new BorderLayout());
        centerPan.add(splitPane, "Center");
        centerPan.add(titlePan, "North");
        homeFrame.add(centerPan);
        homeFrame.setVisible(true);
        messageTf.requestFocus();
        setClientsPan();
    }

    public void setClientsPan() {
        if (friends != null) {
            listModel.clear();
            ListIterator iter = friends.listIterator();
            while (iter.hasNext()) {
                listModel.addElement(((Friend) iter.next()).getName());
            }
            System.out.println("setup clients pan complete");
        }
    }

    @Override
    public void actionPerformed(ActionEvent axnEve) {
        Object obj = axnEve.getSource();
        if (obj == messageTf) {
            if (!messageTf.getText().equals("")) {
                try {
                    objWriter.writeObject(new String(messageTf.getText()));
                } catch (IOException ioExc) {
                    JOptionPane.showMessageDialog(Home.homeFrame, "Server Went Offline");
                }
                messageTf.setText("");
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent clickEvent) {
        int selection = clientsList.getSelectedIndex();
        if (selection >= 0) {
            Friend friend = (Friend) friends.get(selection);
            if (!friend.isActive()) {
                friend.showFriendScreen();
            }
            friend.friendFrame.setVisible(true);
        }
    }
}
