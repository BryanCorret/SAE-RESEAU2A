package SRC;
public class Executable {
    public static void main(String[] args) {
        
        Client c1 = new Client("Alice", "1234");
        Client c2 = new Client("Bob", "4321"); 

        System.out.println(c1);
        System.out.println(c2);

        Messagerie m = new Messagerie();
        m.ajouterClient(c1);
        m.ajouterClient(c2);

        System.out.println(m);

        m.afficherClients();

        
    }
}
