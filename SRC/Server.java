package SRC;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        try{
        ServerSocket ss = new ServerSocket(6500); // sur le port 6500
        System.out.println("Serveur créé");
        List<HashMap<Socket,String>> ListClient = new ArrayList<HashMap<Socket,String>>();

        while (true) {
            // accepter la connexion du client
            Socket socketClient = ss.accept(); 
            // créer un hash map pour stocker le client et son nom
            HashMap<Socket, String> Hashclient = new HashMap<Socket, String>();
            Hashclient.put(socketClient, "client");
            // ajouter le client dans la liste des clients
            ListClient.add(Hashclient);

            // créer un thread pour le client
            Thread thread = new Thread(new ClientHandler(ListClient,socketClient));
            thread.start();
        }

    }
        catch (IOException e) {
            System.out.println("Erreur de création du serveur");
        }

    }
}