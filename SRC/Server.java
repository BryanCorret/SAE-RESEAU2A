import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class Server {
	
	private int uniqueId;// un identifiant unique pour chaque connexion
	private ArrayList<ClientThread> listClient; // une liste pour garder les clients

	private SimpleDateFormat dateFormat; // pour afficher la date
	
	private int port; // le port sur lequel le serveur ecoute

	private boolean running; // pour arreter le serveur

	private ArrayList<String> listeSalon; //la liste des salon

	public Server(int port) {
		this.port = port;
		dateFormat = new SimpleDateFormat("HH:mm");// la date en hh:mm
		listClient = new ArrayList<ClientThread>();
		listeSalon = new ArrayList<String>();
	}
	public ArrayList<String> getListeSalon(){return this.listeSalon;}
	public void ajouterSalon(String salon) {
		this.listeSalon.add(salon);
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public int getUniqueId() {
		return uniqueId;
	}
	public ArrayList<ClientThread> getListClient() {
		return listClient;
	}

	public void start() {
		running = true;
		// créer un socket serveur et attentles connexions
		try
		{
			ServerSocket serverSocket = new ServerSocket(port);// le socket du serveur

			// Permet d'acceuillir des utilisateurs 
			while(running) 
			{
				print("En attente de connexion sur le port :" + port + ".");
				
				Socket socket = serverSocket.accept();// accepter les connexions
				
				if(!running)
					break;
				
				ClientThread t = new ClientThread(socket,uniqueId,dateFormat,listClient,this);// si le serveur est en marche alors créer un thread
				// ajoute le client sur le thread
				listClient.add(t);
				t.start();
			}
			// permet d'arreter le serveur
			try {
				serverSocket.close();
				for(int i = 0; i < listClient.size(); ++i) {
					ClientThread tClient = listClient.get(i);
					try {
					//	ferme les flux et les sockets
					tClient.getIn().close();
					tClient.getOut().close();
					tClient.getSocket().close();
					}
					catch(IOException ioE) {
					}
				}
			}
			catch(Exception e) {
				print("Exception closing the server and clients: " + e);
			}
		}
		catch (IOException e) {
            String msg = dateFormat.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			print(msg);
		}
	}
	
	// pour arreter le serveur
	public void stop() {
		running = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
		}
	}
	
	// pemettre d'afficher les messages avec la date
	public void print(String msg) {
		String time = dateFormat.format(new Date()) + " " + msg;
		System.out.println(time);
	}
	
	// pour envoyer un message à tous les clients
	public synchronized boolean envoieClient(String message, String salon, ClientThread expediteur) {
		// ajoute la date
		String time = dateFormat.format(new Date());
		
		// verifie si le message est privé 
		String[] mp = message.split(" ",3);

		if (message.equals("")) {return false;}

		boolean isPrivate = false;
		if(mp[1].charAt(0)=='@') 
			isPrivate=true;
		
		//si le message est privé alors on envoie le message au destinataire
		if(isPrivate==true)
		{
			String tocheck=mp[1].substring(1, mp[1].length()); // on récupere le nom du destinataire
			
			message=mp[0]+mp[2]; // on reconstruit le message
			String messageLf = "(Privé) "+ time + " " + message; // on formate le message
			boolean found=false;
			
			for(int y=listClient.size(); --y>=0;) // nous parcourons la liste en sens inverse au cas où nous devrions déconnecter un clier un client
			{
				ClientThread ct1=listClient.get(y);
				String check=ct1.getNomUtilisateur();
				if(check.equals(tocheck)){
					// si le destinataire est trouvé on envoie le message
					if(!ct1.writeMsg(messageLf)) {
						listClient.remove(y);
						print("Le client  " + ct1.getNomUtilisateur() + " est déconnecter . "); // permert de supprimer le client dans le cas ou le terminal et fermer ou CTRL+C
					}
					found=true;
					break;
				}

			}
			return found;
		}
		else
		{
			String messageLf = time + " " + message;
			
			System.out.println(messageLf);
			
			// on boucle dans l'ordre inverse si il faudrait supprimer un Client
			for(int i = listClient.size(); --i >= 0;) {
				ClientThread ct = listClient.get(i);
				// on regarde si le client est dans le même channel
				if(ct.getSalon().equals(salon) && !ct.equals(expediteur)) {
					// on envoie le message à au client si il y a une erreur on le supprime
					if(!ct.writeMsg(messageLf)) {
						listClient.remove(i);
						print("Le client  " + ct.getNomUtilisateur() + " est déconnecter . ");
					}
				}
			}
		}
		return true;
	}

	// si un client se déconnecte on le supprime de la liste
	public synchronized void remove(int id) {
		
		String clientdeco = "";
		
		for(int i = 0; i < listClient.size(); ++i) {// on boucle dans la liste
			ClientThread ct = listClient.get(i);
			// si le client est trouvé on le supprime
			if(ct.getId() == id) {
				clientdeco = ct.getNomUtilisateur();
				listClient.remove(i);
				break;
			}
		}
		envoieClient(clientdeco + " à quitte le chat.","", null);
	}
	
	// le thread du serveur
	public static void main(String[] args) {
		// on démarre le serveur sur le port 1500 par défault
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Le port doit être constituer de numéro.");
					System.out.println("Rappel pour changer le port par défault faite: > java Server [port] sans les crochets");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Rappel pour changer le port par défault faite: java Server [port] sans les crochets");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}
}