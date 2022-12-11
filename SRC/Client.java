package SRC;
public class Client{

    private String nom;
    private String password;

    public Client(String nom, String password){
        this.nom = nom;
        this.password = password;
    }

    public String getNom(){
        return this.nom;
    }

    public String getPassword(){
        return this.password;
    }

    @Override
    public String toString(){
        return "Client [nom=" + nom + ", password=" + password + "]";
    }

}
