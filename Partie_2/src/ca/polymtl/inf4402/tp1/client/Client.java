package ca.polymtl.inf4402.tp1.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import ca.polymtl.inf4402.tp1.shared.Fichier;
import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Client {
	
	public static void main(String[] args) {
		Client client = new Client();
		
		if (args.length > 0) {
			

			functions f = functions.valueOf(args[1]);
			switch (f){
			case list:
				client.list();
				break;
			case create:
				client.create(args[2]);
				break;
			case get:
				client.get(args[2]);
				break;
			case push:
				client.push(args[2]);
				break;
			case lock:
				client.lock(args[2]);
				break;
			default:
					System.out.println("Mauvaise command");
			}
		}else
			System.out.println("entrer une command comme argument");
	}

	private String path;
	private int clientId;
	private ServerInterface localServerStub = null;
	private enum functions { list, create, get, push, lock};

	public Client() {
		super();
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServerStub = loadServerStub("127.0.0.1");
		
		this.path = "";
		// On s'authentifie au serveur. On récupère l'ID de notre Client que l'on store dans un fichier.
		this.readClientIdFromFile();
		try {
			this.clientId = localServerStub.generateclientid(this.clientId);
			System.out.println("Votre ID client :"+this.clientId);
			this.writeClientIdInFile();
		} catch (RemoteException e) { e.printStackTrace(); }
	}	

	private void lock(String nom) {
		try {
			Fichier file = localServerStub.lock(nom, this.clientId);
			if (file == null) {
				Fichier f = localServerStub.get(nom);
				if (f == null) {
					System.out.println("Echec: le fichier "+nom+" n'existe pas");
				}else{
					System.out.println("Echec: le fichier "+nom+" appartient au client #"+f.getClientId());
				}
			}else{
				file.writeInFile(path); // on sauvegarde notre fichier sur le disque dur.
				System.out.println(file.getNomFichier()+" a bien été vérouillé et récupéré. Vous pouvez désormais l'éditer.");
			}
		} catch (RemoteException e) { System.out.println("Erreur: " + e.getMessage()); }
	}

	private void push(String nom) {
		try {
			Fichier f = localServerStub.get(nom);
			if (f == null) {
				System.out.println("Echec: le fichier "+nom+" n'existe pas");
			}else if(f.getClientId() == 0){
				System.out.println("Echec: Vous devez vérouiller (lock) votre fichier pour pouvoir faire un push");
			}else if(f.getClientId() > 0 && f.getClientId() != this.clientId){
				System.out.println("Echec: le fichier "+nom+" appartient au client #"+f.getClientId());
			}else{
				Fichier file = new Fichier(nom, path);
				try {
					if(localServerStub.push(nom, file.getFilecontent(), this.clientId))
						System.out.println("Le fichier "+nom+" a bien été mis à jour");
					else{
						System.out.println("Erreur imprévue");
					}
				} catch (RemoteException e) { System.out.println("Erreur: " + e.getMessage()); }
			}
		} catch (RemoteException e) { System.out.println("Erreur: " + e.getMessage()); }
	}

	private void get(String nom) {
		try {
			Fichier file = localServerStub.get(nom);
			if (file == null) {
				System.out.println("Echec: le fichier "+nom+" n'existe pas");
			}else{
				file.writeInFile(path); // on sauvegarde notre fichier sur le disque dur.
				System.out.println(file.getNomFichier()+" a bien été récupéré");
			}
			
		} catch (RemoteException e) { System.out.println("Erreur: " + e.getMessage()); }
	}

	private void create(String nom) {
		try {
			if(localServerStub.create(nom)){
				this.get(nom);
				System.out.println("Le fichier "+nom+" a bien été ajouté");	
			}else
				System.out.println("Echec: le fichier "+nom+" existe déjà");
		} catch (RemoteException e) { System.out.println("Erreur: " + e.getMessage()); }
	}

	private void list() {
		try {
			Hashtable<String, Integer> fileList = localServerStub.list();
			if (fileList.isEmpty()) {
				System.out.println("0 fichiers");
			}else{
				System.out.println(fileList.size()+" fichier"+(fileList.size()>1?"s":"")+" :");
				
				// On récupère les clés de notre Hashtable, id est les fileNames
				Set<String> set = fileList.keySet();
				String fileName;
			    Iterator<String> fileNames = set.iterator();
			    while (fileNames.hasNext()) {
			    	fileName = fileNames.next();
			      System.out.println("* "+fileName + " - #" + fileList.get(fileName));
			    }
			}
		} catch (RemoteException e) { System.out.println("Erreur: " + e.getMessage()); }
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
	private void readClientIdFromFile() {
		Properties properties = new Properties();
//		if (!Files.exists(Paths.get(this.path))) { try {
//			Files.createDirectory(Paths.get(this.path));
//		} catch (IOException e2) { e2.printStackTrace(); } }
		File f = new File("client.properties");
        if (!f.exists()) try {
        	f.createNewFile();
        	this.clientId = 0;this.writeClientIdInFile();return;
        } catch (IOException e1) { e1.printStackTrace();}
        FileInputStream fileInputStream = null;
        
        //Ouverture du fichier
		try {
			fileInputStream = new FileInputStream(f);
			properties.load(fileInputStream);
			fileInputStream.close();
		} catch (IOException e) { e.printStackTrace(); }
        
        if (fileInputStream != null) {
        	this.clientId = properties.isEmpty() ? 0 : Integer.valueOf(properties.getProperty("ClientID"));
        }else{
        	this.clientId = 0;
        }
	}
	private void writeClientIdInFile() {
        Properties properties = new Properties();
        properties.setProperty("ClientID", Integer.toString(this.clientId));

        //Store in the properties file
        File f = new File("client.properties");
        try {
			FileOutputStream fileOutputStream = new FileOutputStream(f);
			properties.store(fileOutputStream, null);
			fileOutputStream.close();
		} catch (FileNotFoundException e) { e.printStackTrace(); }
          catch (IOException e) { e.printStackTrace(); }
	}
}
