package SRC;
import java.util.*;
public class Messagerie {
    
    private ArrayList<Client> clients;

    public Messagerie(){
        this.clients = new ArrayList<>();
    }

    public void ajouterClient(Client c){
        this.clients.add(c);
    }

    public void afficherClients(){
        for(int i = 0; i < this.clients.size(); i++){
            System.out.println(this.clients.get(i));
        }
    }

    public ArrayList<Client> getClients(){
        return this.clients;
    }

    @Override
    public String toString(){
        String msg = "";
        for(int i = 0; i < this.clients.size(); i++){
            msg += this.clients.get(i);
        }
        return msg;
    }
}
