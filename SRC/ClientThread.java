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
	private Server server; // le serveur
	
	private String nomUtilisateur;
	private String salon;
	private Commande cm; // message du client
	private String date; // en timestamp

	private ArrayList<ClientThread> listClient; // liste des clients
	private SimpleDateFormat dateFormat; // pour formater la date

	// CONSTRUCTEUR
	public ClientThread(Socket socket, int clientid,SimpleDateFormat dateFormat, ArrayList<ClientThread> listClient, Server server) {
		this.id = ++clientid; // l'id
		this.listClient = listClient;
		this.socket = socket;
		this.dateFormat = dateFormat;
		this.server = server;
		this.salon = "";
		try
		{
			out = new ObjectOutputStream(socket.getOutputStream());
			in  = new ObjectInputStream(socket.getInputStream());
			nomUtilisateur = (String) in.readObject(); // on récupère le nom du client
			this.server.envoieClient(nomUtilisateur + " à rejoint le chat dite lui bienvenue." , "", this);
		}
		catch (IOException e) {
			print("Erreur de création des outputs/input stream " + e);
			return;
		}
		catch (ClassNotFoundException e) {
		}
		date = new Date().toString();
	}

	public void print(String msg) {
		String time = dateFormat.format(new Date()) + " " + msg;
		System.out.println(time);
	}
	
	public String getNomUtilisateur() {
		return nomUtilisateur;
	}

	public String getSalon() {
		return salon;
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
				boolean confirmation =  this.server.envoieClient(nomUtilisateur + ": " + message, salon, this);
				if(!confirmation){
					writeMsg("Désolé l'utilisateur n'existe pas ou message vide.");
				}
				break;
			case Commande.LOGOUT:
				print(nomUtilisateur + " s'est déconecter.");
				keepGoing = false;
				break;
			case Commande.WHO_HERE:
				writeMsg("Voici la liste des personnes connecter " + dateFormat.format(new Date()));

				if(message.equals("")) {
					for(int i = 0; i < listClient.size(); ++i) { // parcours de la liste des clients
						ClientThread ct = listClient.get(i);
						writeMsg((i+1) + ") " + ct.nomUtilisateur + " since " + ct.date);
					}
				} else {
					for(int i = 0; i < listClient.size(); ++i) { // parcours de la liste des clients
						ClientThread ct = listClient.get(i);
						if(ct.getSalon().equals(message)) {
							writeMsg((i+1) + ") " + ct.nomUtilisateur + " since " + ct.date);
						}
					}
				}
				break;
			case Commande.CREATEROOM:
				this.server.ajouterSalon(message);
				writeMsg("le salon a bien été créé");
				System.out.println(nomUtilisateur+" a créé un salon "+ message);
				break;
			case Commande.JOINROOM:
				if(this.server.getListeSalon().contains(message)) {
					this.salon = message;
					System.out.println(nomUtilisateur+" à rejoint le salon "+ message);
					writeMsg("vous avez rejoint le salon "+ message);
				} else {
					System.out.println(nomUtilisateur+ " a essayer de rejoindre un salon qui n'existe pas");
					writeMsg("Le salon n'existe pas");
				}
				break;
			case Commande.DELETEROOM:
				if(this.server.getListeSalon().contains(message)) {
					this.server.getListeSalon().remove(message);
					this.salon = "";
					System.out.println(nomUtilisateur+" a supprimé le salon "+message);
					writeMsg("le salon "+message+" a bien été suprimé");
				} else {
					System.out.println(nomUtilisateur+ " a essayer de supprimé un salon qui n'existe pas");
					writeMsg("Le salon n'existe pas");
				}
				break;
			case Commande.DISPLAYROOM:
				writeMsg("Voici la liste des salon ouvert :");
				for (String salon : server.getListeSalon()) {
					writeMsg("	"+salon);
				}
			}
		}
		// si l'utilisateur s'est déconnecter on le supprime de la liste
		remove(id);
		close();
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
		this.server.envoieClient(clientdeco + " à quitte le chat.", "", this);
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

	public boolean writeMsg(String msg) { // permet d'envoyer un String au client en local ( sans passer par le server )
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

