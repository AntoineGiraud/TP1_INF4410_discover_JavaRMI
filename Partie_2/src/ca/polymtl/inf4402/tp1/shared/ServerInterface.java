package ca.polymtl.inf4402.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * <p>Le serveur doit exposer aux clients les six opérations : 
 * <ul>
 * 	 <li><strong>generateclientid()</strong></li>
 * 	 <li><em>generateclientid(int clientId) - non exigée mais nous simplifie la vie pour la gestion de l'identifiant du client.</em></li>
 *   <li><strong>create</strong>(String nom)</li>
 *   <li>Hashtable<String, Integer> <strong>list</strong>()</li>
 *   <li>Fichier <strong>get</strong>(String nom)</li>
 *   <li>Fichier <strong>lock</strong>(String nom, int clientid)</li>
 *   <li>boolean <strong>push</strong>(String nom, byte[] contenu, int clientid)</li>
 * </ul></p>
 * @author Antoine Giraud #1761581
 *
 */
public interface ServerInterface extends Remote {
	
	/**
	 * <p>Génère un identifiant unique pour le client.<br>
	 * Celui-ci est sauvegardé dans un fichier local et est retransmis au serveur lors de l'appel à lock() ou push().<br>
	 * Cette méthode est destinée à être appelée par l'application client lorsque nécessaire (il n'y a pas de commande generateclientid visible à l'utilisateur).</p>
	 * @return int ID du nouveau client
	 * @throws RemoteException
	 */
	int generateclientid() throws RemoteException;
	/**
	 * <p>Vérifie si le client est connu. Si oui on lui retourne son même identifiant. Sinon, on lui en génère un nouveau identifiant.<br>
	 * On utilise la méthode précédente pour générer l'identifiant unique.</p>
	 * @param clientId
	 * @return int ID du nouveau client
	 * @throws RemoteException
	 */
	int generateclientid(int clientId) throws RemoteException;
	
	/**
	 * <p>Crée un fichier vide sur le serveur avec le nom spécifié.<br>
	 * Si un fichier portant ce nom existe déjà, l'opération échoue.</p>
	 * @param nom Nom du fichier à créer
	 * @return boolean Est ce que l'on a réussi ou non à créer le fichier. false si un fichier du même nom existe déjà.
	 * @throws RemoteException
	 */
	boolean create(String nom) throws RemoteException;
	
	/**
	 * <p>Retourne la liste des fichiers présents sur le serveur.<br>
	 * Pour chaque fichier, le nom du fichier et l'identifiant du client possédant le verrou (le cas échéant) sont retournés.</p>
	 * @return String[] liste des fichiers du serveur {nomDuFichier, identifiantClientPssoedantFichier}
	 * @throws RemoteException
	 */
	Hashtable<String, Integer> list() throws RemoteException;

	/**
	 * <p>Demande au serveur d'envoyer la dernière version du fichier spécifié.<br>
	 * Le fichier est écrit dans le répertoire local courant.</p>
	 * @param nom Nom du fichier que l'on recherche
	 * @return File le fichier avec tous ses attributs ou null si on ne l'a pas trouvé.
	 * @throws RemoteException
	 */
	Fichier get(String nom) throws RemoteException;
	
	/**
	 * <p>Demande au serveur de verrouiller le fichier spécifié.<br>
	 * La dernière version du fichier est écrite dans le répertoire local courant.<br>
	 * L'opération échoue si le fichier est déjà verrouillé par un autre client.</p>
	 * @param nom
	 * @param clientid
	 * @return File le fichier avec tous ses attributs ou null si on ne l'a pas trouvé ou si qqn l'a déjà vérouillé.
	 * @throws RemoteException
	 */
	Fichier lock(String nom, int clientid) throws RemoteException;

	/**
	 * <p>Envoie une nouvelle version du fichier spécifié au serveur.<br>
	 * L'opération échoue si le fichier n'avait pas été verrouillé par le client préalablement.<br>
	 * Si le push réussit, le contenu envoyé par le client remplace le contenu qui était sur le serveur auparavant et le fichier est déverrouillé.</p>
	 * @param nom
	 * @param contenu
	 * @param clientid
	 * @return boolean réponse sur le succès ou non de l'opération
	 * @throws RemoteException
	 */
	boolean push(String nom, byte[] contenu, int clientid) throws RemoteException;
}
