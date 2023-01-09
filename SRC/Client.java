package SRC;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client{
    
    private String nom;
    private Socket socket;


    public Client(String nom, String ip) {
        try{
            this.nom = nom;
            socket = new Socket(ip, 6500);

        } catch (IOException exception) {
            System.out.println(exception);
        }
    }

    public String getNom() {
        return nom;
    }

    public Socket getSocket() {
        return socket;
    }


    @Override
    public String toString() {
        return "Client " + nom;
    }



    public void ecrireMessage() throws IOException {
        // demander un message a l'utilisateur
        Scanner sc = new Scanner(System.in);
        String message = sc.nextLine();
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(this.nom+"/"+message);
        out.flush();
        sc.close();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Entrer l'ip du serveur : ");
            String ip = sc.nextLine();
            System.out.println("Entrer votre pseudo ");
            String pseudo = sc.nextLine();
            Client client = new Client(pseudo, ip);

            System.out.println(client);

            while (true) {
                client.ecrireMessage();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        sc.close();
    }
}
