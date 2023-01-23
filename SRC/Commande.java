
import java.io.*;

public class Commande implements Serializable {

	// WHO_HERE recevoir la liste des utilisateurs connectés
	// MESSAGE message privée
	// LOGOUT se déconnecter
	static final int WHO_HERE = 0;
	static final int MESSAGE = 1;
	static final int LOGOUT = 2;
	static final int CREATEROOM = 3;
	static final int DELETEROOM = 4;
	static final int JOINROOM = 5;
	static final int DISPLAYROOM = 6;

	private int type;
	private String message;
	
	// constructor
	Commande(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	int getType() {
		return type;
	}

	String getMessage() {
		return message;
	}
}
