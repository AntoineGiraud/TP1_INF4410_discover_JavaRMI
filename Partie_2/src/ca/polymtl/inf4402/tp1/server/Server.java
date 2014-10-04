package ca.polymtl.inf4402.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;

import ca.polymtl.inf4402.tp1.shared.Fichier;
import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
	
	private ArrayList<Fichier> ListeFichiers;
	private Integer lastClientId;

	public Server() {
		super();
		ListeFichiers = new ArrayList<Fichier>();
		lastClientId = 0;
	}

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
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	@Override
	public int generateclientid() {
		return lastClientId++;
	}

	/*
	 * Méthode accessible par RMI. Additionne les deux nombres passés en
	 * paramètre.
	 */
	@Override
	public int execute(int a, int b) throws RemoteException {
		return a + b;
	}

	/*
	 * Autre méthode accessible par RMI. Elle prend un tableau afin de pouvoir
	 * lui envoyer des arguments de taille variable.
	 */
	@Override
	public void execute(byte[] arg) throws RemoteException {
		return;
	}

	@Override
	public boolean create(String nom) throws RemoteException {
		for (Fichier f : ListeFichiers) {
			if (f.getNomFichier() == nom)
				return false;
		}
		ListeFichiers.add(new Fichier(nom));
		return true;
	}

	@Override
	public Hashtable<String, Integer> list() throws RemoteException {
		Hashtable<String, Integer> liste = new Hashtable<String, Integer>();
		for (Fichier f : ListeFichiers) {
			liste.put(f.getNomFichier(), f.getClientId());
		}
		return liste;
	}

	@Override
	public Fichier get(String nom) throws RemoteException {
		for (Fichier f : ListeFichiers) {
			if (f.getNomFichier() == nom)
				return f;
		}
		return null;
	}

	@Override
	public Fichier lock(String nom, int clientid) throws RemoteException {
		for (int i = 0; i < ListeFichiers.size(); i++) {
			Fichier f = ListeFichiers.get(i);
			if (f.getNomFichier() == nom && f.lockFile(clientid)){ // Si on a le même nom de fichier, et si on arrive à locker le file c'est bon
				ListeFichiers.set(i, f); // On met à jour notre fichier que l'on vient de locker au client voulu
				return f;
			}
		}
		return null;
	}

	@Override
	public boolean push(String nom, byte[] contenu, int clientid) throws RemoteException {
		for (int i = 0; i < ListeFichiers.size(); i++) {
			Fichier f = ListeFichiers.get(i);
			if (f.getNomFichier() == nom && f.getClientId() == clientid){
				f.setFilecontent(contenu);
				f.unlockFile();
				ListeFichiers.set(i, f); // On met à jour notre fichier que l'on vient de locker au client voulu
				return true;
			}
		}
		return false;
	}
}
