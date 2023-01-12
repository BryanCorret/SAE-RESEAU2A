
import java.net.*;
import java.io.*;
import java.util.*;

public class Client  {

	private ObjectInputStream in;		
	private ObjectOutputStream out;		
	private Socket socket;					
	
	private String server, nomUtilisateur;	
	private int port;					

	public String getNomUtilisateur() {
		return nomUtilisateur;
	}

	public void setNomUtilisateur(String username) {
		this.nomUtilisateur = username;
	}

	public Client(String server, int port, String nomUtilisateur) {
		this.server = server;
		this.nomUtilisateur = nomUtilisateur;
		this.port = port;
	}
	
	// démarer le client et commencer a parler
	public boolean start() {
		// On essaie de se co au server
		try {
			socket = new Socket(server, port);
		} 
		// cas ou il y a une erreur
		catch(Exception ec) {
			System.out.println("Impossible de se connecter au server" + ec);
			return false;
		}
		
		String msg = "Connection reussi " + socket.getInetAddress() + ":" + socket.getPort();
		System.out.println(msg);
	
		try
		{
			in  = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			System.out.println("Impossible de créer Input/output : " + eIO);
			return false;
		}

		// création du thread pour ecouter le server
		new Ecoute(in).start();
		// Envoie du client au server
	
		try
		{
			out.writeObject(nomUtilisateur);
		}
		catch (IOException eIO) {
			System.out.println("Le client n'a pas pu être récu par le server : " + eIO);
			deco();
			return false;
		}
		return true;
	}
	
	// pour envoyer un message au server
	void envoieMsg(Commande msg) {
		try {
			out.writeObject(msg);
		}
		catch(IOException e) {
			System.out.println("Le server n'a pas pu accepter votre message: " + e);
		}
	}

	// cas ou on veut se deconnecter
	private void deco() {
		try { 
			if(in != null) in.close(); //si le flux n'est pas null alors je ferme le flux d'entrée
		}
		catch(Exception e) {}
		try {
			if(out != null) out.close(); // si le flux n'est pas null alors je ferme le flux de sortie
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close(); // si le socket n'est pas null alors je ferme le socket
		}
		catch(Exception e) {}
			
	}

	public static void main(String[] args) {
		// Par défault  les valeurs
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Mystere";
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Rentré votre pseudo: ");
		userName = scan.nextLine();

		// Cas ou il y a l'utilisateur rentre mal les arguments
		switch(args.length) {
			case 3:
				// for > javac Client username portNumber serverAddr
				serverAddress = args[2];
			case 2:
				// for > javac Client username portNumber
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Port invalide merci de mettre un nombre");
					System.out.println("Pour rappel l'ordre est comme ça [username] [portNumber] [serverAddress] sans les crochets");
					return;
				}
			case 1: //
				userName = args[0];
			case 0:
				// cas ou le nombre est plus peti
				break;
			// Si le nombre d'argument est plus grand que 3 
			default:
				System.out.println("Pour rappel l'ordre est comme ça [username] [portNumber] [serverAddress] sans les crochets");
			return;
		}
		
		Client client = new Client(serverAddress, portNumber, userName);
		// essaie de connecter sinon on quitte
		if(!client.start())
			return;
		
		System.out.println("\nBienvenue sur .! Le lieu de discussion des nerds.");
		System.out.println("Voici un bref résumer:");
		System.out.println("1. Par défault quand vous envoyer un message, vous l'envoyer à tout le monde");
		System.out.println("2. Pour envoyer un message privé faite '@nomutilisater votre-message' sans les cotes évidemment :)");
		System.out.println("3. Pour savoir qui est présent dans le salon fait la commandes 'WHO_HERE' sans les cotes encore une fois");
		System.out.println("4. Enfin pour te déconnecter en toute tranquilité fait 'LOGOUT' ");
		
		while(true) {
			System.out.print("> ");
			// Lis le msg 
			String msg = scan.nextLine();
			//Si le msg est LOUGOUT permet de se déconnecter
			if(msg.equals("WHO_HERE") ) {
				client.envoieMsg(new Commande(Commande.WHO_HERE, ""));	
				break;
			}
			// Si c'est la commande message WHO_HERE permet de savoir qui est présent 
			else if(msg.equals("LOGOUT")) {
				client.envoieMsg(new Commande(Commande.LOGOUT, ""));
			}
			// msg 
			else {
				client.envoieMsg(new Commande(Commande.MESSAGE, msg));
			}
		}
		scan.close();// ferme la lecture de la console
		client.deco(); // deconnecte le clien	
	}

}

