package core;

/**
 *
 * @author UlTMATE
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Home implements ActionListener {
    
    public static JFrame homeFrame;
    public static JPanel centerPan, topPan;
    JTextField ipTf, nameTf;
    JButton goBut;
    static Client client;
    public static ChatScreen chScr;
    public static String serverIp;
    
    public Home(){
        createGUI();
    }
    
    public void createGUI(){
        homeFrame = new JFrame("uChat");
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        homeFrame.setSize(dim.width>>2, dim.height>>1);
        homeFrame.setLocationRelativeTo(null);
        try{
            for(UIManager.LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()){
                if("Nimbus".equals(info.getName())){
                    UIManager.setLookAndFeel(info.getClassName()); 
                }
            }
        } catch(Exception exc){
            try{
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch(Exception exc2){
                
            }
        }
        showWelcome();
    }
    
    public void showWelcome(){
        topPan = new JPanel(new GridBagLayout());
        topPan.setBackground(Color.darkGray);
        JLabel ipLab = new JLabel("Server IP");
        ipLab.setForeground(Color.yellow);
        ipTf = new JTextField("localhost",10);
        ipTf.addActionListener(this);
        topPan.add(ipLab);
        topPan.add(Box.createRigidArea(new Dimension(5,0)));
        topPan.add(ipTf);
        
        centerPan = new JPanel(new GridBagLayout());
        centerPan.setBackground(Color.darkGray);
        GridBagConstraints gbc = new GridBagConstraints();
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        
        JLabel nameLab = new JLabel("Name");
        nameLab.setForeground(Color.yellow);
        nameLab.setFont(font);
        nameTf = new JTextField("",10);
        nameTf.addActionListener(this);
        nameTf.setFont(font);
        goBut = new JButton("Chat");
        goBut.setFont(font);
        goBut.setForeground(Color.green);
        goBut.setBackground(Color.darkGray);
        goBut.addActionListener(this);
        
        gbc.gridwidth=3; gbc.anchor=GridBagConstraints.WEST;
        gbc.insets = new Insets(5,5,5,5);
        centerPan.add(nameLab, gbc);
        gbc.gridx=4;
        centerPan.add(nameTf, gbc);
        gbc.gridx=3; gbc.gridy=2; gbc.gridwidth=4; gbc.anchor = GridBagConstraints.CENTER;
        centerPan.add(goBut, gbc);
        
        homeFrame.add(topPan, "North");
        homeFrame.add(centerPan, "Center");
        homeFrame.setVisible(true);
        nameTf.requestFocus();
    }
    
    @Override
    public void actionPerformed(ActionEvent axnEve) {
        Object obj = axnEve.getSource();
        if (obj == ipTf) {
            nameTf.requestFocus();
        } else if (obj == goBut || obj == nameTf) {
            if (ipTf.getText().equals("")) {
                JOptionPane.showMessageDialog(homeFrame, "Please Provide Server's IP");
            } else if (nameTf.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please Provide Your Name");
            } else {
                this.serverIp = ipTf.getText();
                client = new Client(nameTf.getText());
                chScr = new ChatScreen();
            }
        } 
    }
    
    public static void main(String args[]){
        new Home();
    }
}
