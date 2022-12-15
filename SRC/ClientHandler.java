package SRC;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class ClientHandler implements Runnable {
    private List<HashMap<Socket,String>> ListClient;
    private Socket socket;

    public ClientHandler(List<HashMap<Socket, String>> ListClient, Socket socket) {
        this.ListClient = ListClient;
        this.socket = socket;
        
    }

    @Override
    public void run() {
        while (true) {
            
            // lire le message du client
                String message = null;
            try {

                    DataInputStream in = new DataInputStream(this.socket.getInputStream()); // lire le message du client
                    message = in.readUTF(); // lire le message du client
                    System.out.println("client : " + message); // afficher le message du client
                    
                    // envoyer un message a tous les clients
                    
                    for (HashMap<Socket,String> c : ListClient) {
                        Socket keySocket = c.keySet().iterator().next();
                        DataOutputStream out = new DataOutputStream(keySocket.getOutputStream());
                        out.writeUTF(message);
                        out.flush();
                    }
                
            } catch (IOException e) {
                    e.printStackTrace();
                }       
                
                
            }
        }
    }

