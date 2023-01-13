import java.io.IOException;
import java.io.ObjectInputStream;

// Permet d'attendre la réception des messages	
public class Ecoute extends Thread {
    private ObjectInputStream in;

    public Ecoute(ObjectInputStream in) {
        this.in = in;
    }
    public void run() {
		while(true) {
			try {
				
				String msg = (String) in.readObject(); // lis le msg
				System.out.println(msg);

                // permet de mettre un caractere pour le message
				System.out.print("> ");
			}
			catch(IOException e) {
				System.out.println("Le server à fermer la connexion: " + e);
				break;
			}
			catch(ClassNotFoundException e2) {
				System.out.println("Class pas trouvée " + e2);
			
            }
		    }
	}
}
