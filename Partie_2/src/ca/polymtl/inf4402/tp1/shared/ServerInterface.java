package ca.polymtl.inf4402.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

public interface ServerInterface extends Remote {
	
	/**
	 * Génère un identifiant unique pour le client.
	 * Celui-ci est sauvegardé dans un fichier local et est retransmis au serveur lors de l'appel à lock() ou push().
	 * Cette méthode est destinée à être appelée par l'application client lorsque nécessaire (il n'y a pas de commande generateclientid visible à l'utilisateur).
	 * @return int ID du nouveau client
	 */
	int generateclientid() throws RemoteException;
	
	/**
	 * Crée un fichier vide sur le serveur avec le nom spécifié.
	 * Si un fichier portant ce nom existe déjà, l'opération échoue.
	 * @param nom Nom du fichier à créer
	 * @return boolean Est ce que l'on a réussi ou non à créer le fichier. false si un fichier du même nom existe déjà.
	 * @throws RemoteException
	 */
	boolean create(String nom) throws RemoteException;
	
	/**
	 * Retourne la liste des fichiers présents sur le serveur.
	 * Pour chaque fichier, le nom du fichier et l'identifiant du client possédant le verrou (le cas échéant) sont retournés.
	 * @return String[] liste des fichiers du serveur {nomDuFichier, identifiantClientPssoedantFichier}
	 * @throws RemoteException
	 */
	Hashtable<String, Integer> list() throws RemoteException;

	/**
	 * Demande au serveur d'envoyer la dernière version du fichier spécifié.
	 * Le fichier est écrit dans le répertoire local courant.
	 * @param nom Nom du fichier que l'on recherche
	 * @return File le fichier avec tous ses attributs.
	 * @throws RemoteException
	 */
	Fichier get(String nom) throws RemoteException;
	
	/**
	 * Demande au serveur de verrouiller le fichier spécifié.
	 * La dernière version du fichier est écrite dans le répertoire local courant.
	 * L'opération échoue si le fichier est déjà verrouillé par un autre client.
	 * @param nom
	 * @param clientid
	 * @return File le fichier avec tous ses attributs.
	 * @throws RemoteException
	 */
	Fichier lock(String nom, int clientid) throws RemoteException;

	/**
	 * Envoie une nouvelle version du fichier spécifié au serveur.
	 * L'opération échoue si le fichier n'avait pas été verrouillé par le client préalablement.
	 * Si le push réussit, le contenu envoyé par le client remplace le contenu qui était sur le serveur auparavant et le fichier est déverrouillé.
	 * @param nom
	 * @param contenu
	 * @param clientid
	 * @return boolean réponse sur le succès ou non de l'opération
	 * @throws RemoteException
	 */
	boolean push(String nom, byte[] contenu, int clientid) throws RemoteException;

	int execute(int a, int b) throws RemoteException;
	void execute(byte[] arg) throws RemoteException;
}
