
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


// Permet de faire un thread pour chaque client
public class ClientThread extends Thread {
	// the socket to get messages from client
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private int id; // le numéro du client
	
	private String nomUtilisateur;
	private Commande cm; // message du client
	private String date; // en timestamp

	private ArrayList<ClientThread> listClient; // liste des clients
	private SimpleDateFormat dateFormat; // pour formater la date

	// CONSTRUCTEUR
	public ClientThread(Socket socket, int clientid,SimpleDateFormat dateFormat, ArrayList<ClientThread> listClient) {
		this.id = ++clientid; // l'id
		this.listClient = listClient;
		this.socket = socket;
		this.dateFormat = dateFormat;
		try
		{
			out = new ObjectOutputStream(socket.getOutputStream());
			in  = new ObjectInputStream(socket.getInputStream());
			nomUtilisateur = (String) in.readObject(); // on récupère le nom du client
			envoieClient(nomUtilisateur + " à rejoint le chat dite lui bienvenue." );
		}
		catch (IOException e) {
			print("Erreur de création des outputs/input stream " + e);
			return;
		}
		catch (ClassNotFoundException e) {
		}
		date = new Date().toString() + "\n";
	}

	public void print(String msg) {
		String time = dateFormat.format(new Date()) + " " + msg;
		System.out.println(time);
	}
	
	public String getNomUtilisateur() {
		return nomUtilisateur;
	}

	public void setNomUtilisateur(String username) {
		this.nomUtilisateur = username;
	}
	
	public ArrayList<ClientThread> getListClient() {
		return listClient;
	}

	public Socket getSocket() {
		return socket;
	}

	public ObjectOutputStream getOut() {
		return out;
	}
	public long getId() {
		return id;
	}
	public ObjectInputStream getIn() {
		return in;
	}


	//permet de lire le prochain message
	public void run() {
	
		boolean keepGoing = true;
		while(keepGoing) { // vrai tant que l'utilisateur ne fait pas de LOGOUT
		
			try {
				cm = (Commande) in.readObject();
			}
			catch (IOException e) {
				print(nomUtilisateur + " Impossible de lire l'objet: " + e);
				break;				
			}
			catch(ClassNotFoundException e2) {
				break;
			}
			
			String message = cm.getMessage(); // on récupère le message

		
			switch(cm.getType()) { // on récupère le type de message

			case Commande.MESSAGE: // si le message est privée
				boolean confirmation =  envoieClient(nomUtilisateur + ": " + message);
				if(confirmation==false){
					String msg ="Désolé l'utilisateur n'existe pas.";
					writeMsg(msg);
				}
				break;
			case Commande.LOGOUT:
				print(nomUtilisateur + " s'est déconecter.");
				keepGoing = false;
				break;
			case Commande.WHO_HERE:
				writeMsg("Voici la liste des personnes connecter " + dateFormat.format(new Date()) + "\n");

				for(int i = 0; i < listClient.size(); ++i) { // parcours de la liste des clients
					ClientThread ct = listClient.get(i);
					writeMsg((i+1) + ") " + ct.nomUtilisateur + " since " + ct.date);
				}
				break;
			}
		}
		// si l'utilisateur s'est déconnecter on le supprime de la liste
		remove(id);
		close();
	}


	
	private synchronized boolean envoieClient(String message) {
		// ajoute la date
		String time = dateFormat.format(new Date());
		
		// verifie si le message est privé 
		String[] mp = message.split(" ",3);
		
		boolean isPrivate = false;
		if(mp[1].charAt(0)=='@') 
			isPrivate=true;
		
		//si le message est privé alors on envoie le message au destinataire
		if(isPrivate==true)
		{
			String tocheck=mp[1].substring(1, mp[1].length()); // on récupere le nom du destinataire
			
			message=mp[0]+mp[2]; // on reconstruit le message
			String messageLf = "(Privé) "+ time + " " + message + "\n"; // on formate le message
			boolean found=false;
			
			for(int y=listClient.size(); --y>=0;) // nous parcourons la liste en sens inverse au cas où nous devrions déconnecter un clier un client
			{
				ClientThread ct1=listClient.get(y);
				String check=ct1.getNomUtilisateur();
				if(check.equals(tocheck)){
					// si le destinataire est trouvé on envoie le message
					if(!ct1.writeMsg(messageLf)) {
						listClient.remove(y);
						print("Le client  " + ct1.nomUtilisateur + " est déconnecter . "); // permert de supprimer le client dans le cas ou le terminal et fermer ou CTRL+C
					}
					found=true;
					break;
				}

			}
			if(found!=true){
				return false; 
			}
		}
		// si le message est pour tout le monde
		else
		{
			String messageLf = time + " " + message + "\n";
			
			System.out.print(messageLf);
			
			// on boucle dans l'ordre inverse si il faudrait supprimer un Client
			for(int i = listClient.size(); --i >= 0;) {
				ClientThread ct = listClient.get(i);
				// on envoie le message à tous les clients si il y a une erreur on supprime le client
				if(!ct.writeMsg(messageLf)) {
					listClient.remove(i);
					print("Le client  " + ct.nomUtilisateur + " est déconnecter . ");
				}
			}
		}
		return true;
		
	}

	public synchronized void remove(int id) {
	
		String clientdeco = "";
		
		for(int i = 0; i < listClient.size(); ++i) {// on boucle dans la liste
			ClientThread ct = listClient.get(i);
			// si le client est trouvé on le supprime
			if(ct.id == id) {
				clientdeco = ct.getNomUtilisateur();
				listClient.remove(i);
				break;
			}
		}
		envoieClient(clientdeco + " à quitte le chat.");
	}
	// permet de fermer les flux et le socket
	private void close() {
		try {
			if(out != null) out.close();
		}
		catch(Exception e) {}
		try {
			if(in != null) in.close();
		}
		catch(Exception e) {};
		try {
			if(socket != null) socket.close();
		}
		catch (Exception e) {}
	}

	public boolean writeMsg(String msg) { // permet d'envoyer un String au client
		if(!socket.isConnected()) {// si le client n'est pas connecter on le déconecte
			close();
			return false;
		}
		// on essaie d'envoyer le message le message
		try {
			out.writeObject(msg);
		}
		catch(IOException e) {
			print("Impossible d'envoyer le message désolé ;( " + nomUtilisateur );
			print(e.toString());
		}
		return true;
	}

}

