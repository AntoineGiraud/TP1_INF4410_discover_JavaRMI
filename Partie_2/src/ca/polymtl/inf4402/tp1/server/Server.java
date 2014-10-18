package ca.polymtl.inf4402.tp1.server;

import java.io.File;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;

import ca.polymtl.inf4402.tp1.shared.Fichier;
import ca.polymtl.inf4402.tp1.shared.ServerInterface;

/**
 * <p>confer {@link ServerInterface} pour les explications sur cette classe et ses méthodes.</p>
 * @author Antoine Giraud #1761581
 *
 */
public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
	
	private ArrayList<Fichier> ListeFichiers;
	private ArrayList<Integer> ListeClients;
	private String pathServerFolder;

	public Server() {
		super();
		ListeFichiers = new ArrayList<Fichier>();
		ListeClients = new ArrayList<Integer>();
		pathServerFolder = "server_files";
		
		// Mettre à jour la liste des fichiers présents sur le serveur
        getAllFile(new File(pathServerFolder));
	}
	
	/**
	 * <p>Petite fonction récursive qui va nous permettre de parcourir le répertoire de notre serveur où sont situés les fichiers que l'on reçoit des clients.<br>
	 * Cela va nous permetter de pouvoir garder nos fichiers entre deux sessions.</p>
	 * @param file File notre répertoir et puis ses fichiers - récursivité
	 */
	public void getAllFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
            	ListeFichiers.add(new Fichier(file.getName(), pathServerFolder));
            } else if (file.isDirectory()) {
                File[] tabTmp = file.listFiles();
                for (File f : tabTmp) {
                    getAllFile(f);
                }
            }
        }
    }
    
	/**
	 * Connection au registre RMI et enregistrement de la classe server
	 */
	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	@Override
	public boolean create(String nom) throws RemoteException {
		for (Fichier f : ListeFichiers) {
			if (f.getNomFichier().contentEquals(nom))
				return false;
		}
		ListeFichiers.add(new Fichier(nom, pathServerFolder));
		System.out.println("Fichier "+nom+" ajouté !");
		return true;
	}

	@Override
	public Hashtable<String, Integer> list() throws RemoteException {
		System.out.println("Liste des fichiers : ");
		Hashtable<String, Integer> liste = new Hashtable<String, Integer>();
		for (Fichier f : ListeFichiers) {
			liste.put(f.getNomFichier(), f.getClientId());
			System.out.println("* "+f.getNomFichier()+" - #"+f.getClientId());
		}
		return liste;
	}

	@Override
	public Fichier get(String nom) throws RemoteException {
		for (Fichier f : ListeFichiers) {
			if (f.getNomFichier().equals(nom)){
				return f;
			}
		}
		System.out.println("Fichier "+nom+" non trouvé !");
		return null;
	}

	@Override
	public synchronized Fichier lock(String nom, int clientid) throws RemoteException {
		for (int i = 0; i < ListeFichiers.size(); i++) {
			Fichier f = ListeFichiers.get(i);
			if (f.getNomFichier().equals(nom)){ // Si on a le même nom de fichier, et si on arrive à locker le file c'est bon
				if (f.lockFile(clientid)) {
					ListeFichiers.set(i, f); // On met à jour notre fichier que l'on vient de locker au client voulu
					System.out.println("Fichier "+nom+" locked par #"+clientid);
					return f;
				}else{
					System.out.println("Echec: le fichier "+nom+" appartient au client #"+f.getClientId());return null;
				}
			}
		}
		System.out.println("Fichier "+nom+" non trouvé ! - #"+clientid);
		return null;
	}

	@Override
	public boolean push(String nom, byte[] contenu, int clientid) throws RemoteException {
		for (int i = 0; i < ListeFichiers.size(); i++) {
			Fichier f = ListeFichiers.get(i);
			if (f.getNomFichier().equals(nom) && f.getClientId() == clientid){
				f.setFilecontent(contenu);
				f.unlockFile();
				ListeFichiers.set(i, f); // On met à jour notre fichier que l'on vient de locker au client voulu
				f.writeInFile(pathServerFolder); // on sauvegarde notre fichier sur le disque dur.
				System.out.println("Fichier "+nom+" MAJ & unlocked par #"+clientid);
				return true;
			}
		}
		System.out.println("Fichier "+nom+" non trouvé ! - #"+clientid);
		return false;
	}

	@Override
	public int generateclientid(int clientId) throws RemoteException {
		if (Integer.valueOf(clientId) > 0 && ListeClients.contains(Integer.valueOf(clientId)))
			return clientId;
		else
			return this.generateclientid();
	}
	@Override
	public int generateclientid() {
		int id = this.getRandomNumber();
		while (ListeClients.contains(id)) {
			id = this.getRandomNumber();
		}
		ListeClients.add(id);
		return id;
	}
	/**
	 * Fonction pour générer un nombre aléatoire
	 * @return int random number
	 */
	private int getRandomNumber() {
		return (int) (Math.random()*100000)+1;
	}
}
