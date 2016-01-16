package core;

/**
 *
 * @author UlTMATE
 */
import java.net.*;
import java.util.*;
import java.io.*;

public class MainServer {

    ArrayList clients;
    final int PORT = 8085;
    ServerSocket serverSocket = null;
    //Socket socket = null;

//    ObjectInputStream objReader;
    ObjectOutputStream objWriter;
//    Object clientObj;
    Object input;

    public MainServer() {
        clients = new ArrayList();
        startServer();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server Started");
            while (true) {
//                System.out.println("wait");
              Socket  socket = serverSocket.accept();
//              System.out.println("New Client");
                new Thread(() -> {
                    Object clientObj=null;
                    ObjectInputStream objReader=null;
                    try {
                        objReader = new ObjectInputStream(socket.getInputStream());
                        clientObj = objReader.readObject();
                    } catch (IOException ioExc) {
                        ioExc.printStackTrace();
                    } catch (ClassNotFoundException cnfExc) {
                      
                    }
                    Client clToAdd = (Client)clientObj;
                    clToAdd.setServerPortNumber(socket.getPort());
                    clToAdd.setSocket(socket);
                    try {
                        clToAdd.setSocketOutputStream(socket.getOutputStream());
                    } catch (IOException ioExc) {
                        ioExc.printStackTrace();
                    }
                    clients.add(clToAdd);
                    System.out.println("Client added : "+clToAdd.getName() + ", " +clToAdd.getServerPortNumber());
                    updateClients();
                    while (true) {
                        try {
                            System.out.println("Server is Reading........");
                            input = objReader.readObject();
                            System.out.println("Read Something........");
                            if (input instanceof String) {
                                System.out.println("Input : " + input);
                                ListIterator clientIterator = clients.listIterator();
                                System.out.println("Writing to Clients: " + clients);
                                while (clientIterator.hasNext()) {
                                    Client client = (Client) clientIterator.next();
                                    try {
//                                        InetAddress adrs = InetAddress.getByName("localhost");
                                        objWriter = client.getSocketOutputStream();
                                            objWriter.writeObject(new String(((Client) clientObj).getName() + ": " + input));
                                            System.out.println("Written on " + ((Client) client).getName());
                                        
                                    } catch (UnknownHostException unhoExc) {
                                        System.out.println("Exception in Update Client unho");
                                        unhoExc.printStackTrace();
                                    } catch (IOException ioExc) {
                                        System.out.println("A User is Disconnected");
                                        ioExc.printStackTrace();
                                        updateClients();
                                    }
                                }
                            } else if (input instanceof Friend) {
                                System.out.println("Received a friend object");
                                Friend receivedFriend = (Friend) input;
                                int index = -1;
                                ListIterator liter = clients.listIterator();
                                while (liter.hasNext()) {
                                    Client temp = (Client) liter.next();
                                    System.out.println(receivedFriend.getName() + " " + temp.getName());
                                    System.out.println(receivedFriend.getServerPortNumber() + " " + temp.getServerPortNumber());
                                    if ((receivedFriend.getName().equals(temp.getName())) && (receivedFriend.getServerPortNumber() == temp.getServerPortNumber())) {
                                        index = clients.indexOf(temp);
                                        break;
                                    }
                                }
                                System.out.println("Index is " +index);
                                if (index != -1) {
                                    String field = (String) objReader.readObject();
                                    System.out.println("Read txtfield " + field);
//                                    InetAddress adrs = InetAddress.getByName("localhost");
                                    objWriter = ((Client)clients.get(index)).getSocketOutputStream();
                                        objWriter.writeObject(clToAdd);
                                        objWriter.writeObject(field);
                                    
//                                    objWriter = clToAdd.getSocketOutputStream();
//                                        objWriter.writeObject(clToAdd);
//                                        objWriter.writeObject(field);
                                    
                                }
                            }
                        } catch (IOException ioExc) {
                            System.out.println(((Client) clientObj).getName() + " Disconnected");
                            clients.remove(clientObj);
                            updateClients();
                            ioExc.printStackTrace();
                            break;
                        } catch(ClassNotFoundException cnfExc){
                            
                        }
                    }
                }).start();

            }
        } catch (IOException ioExc) {
//            clients.remove(clientObj);
//            updateClients();
            ioExc.printStackTrace();
        }
    }

    public synchronized void updateClients() {
        new Thread(() -> {
            ListIterator clientIterator = clients.listIterator();
            while (clientIterator.hasNext()) {
                ArrayList tempClientsList = new ArrayList(clients);
                Client client = (Client) clientIterator.next();
                try {
                    InetAddress adrs = InetAddress.getByName("localhost");
                   
                        objWriter = client.getSocketOutputStream();
                        
                        tempClientsList.remove(client);
                        objWriter.writeObject(tempClientsList);

                    
                } catch (UnknownHostException unhoExc) {
                    System.out.println("Exception in Update Client unho");
                    unhoExc.printStackTrace();
                } catch (IOException ioExc) {
                    System.out.println(client.getName() + " is Disconnected while updating");
                    ioExc.printStackTrace();
                    clients.remove(client);
                    updateClients();
                }
            }
        }).start();
    }

    public static void main(String args[]) {
        new MainServer();
    }
}
