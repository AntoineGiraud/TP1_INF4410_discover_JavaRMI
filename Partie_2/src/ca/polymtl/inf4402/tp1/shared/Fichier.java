/**
 * 
 */
package ca.polymtl.inf4402.tp1.shared;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Antoine
 *
 */
public class Fichier implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5848260167280425933L;
	private String nom;
	private Integer clientid;
	private byte[] contenu;
	
	public Fichier(String nom,String path){
		this.nom = nom;
		this.clientid = 0;
		this.setFromFile(path);
	}
	
	public Fichier(Fichier f){
		copyInFile(f);	
	}
	
	public void copyInFile(Fichier f){
		this.nom = f.nom;
		this.clientid = f.clientid;
		this.contenu = f.contenu;
	}
	
	public boolean lockFile(int clientId){
		if(this.clientid == 0 || this.clientid == clientId){ // Le fichier n'appartient pas à qqn (ou à la limite on l'a déjà locké nous même), on peut le locker
			this.clientid = clientId;
			return true;
		}
		return false;
	}
	
	public void unlockFile(){
		this.clientid = 0;
	}
	
	public String getNomFichier(){
		return this.nom;
	}
	public int getClientId(){
		return this.clientid;
	}
	public byte[] getFilecontent(){
		return this.contenu;
	}
	public void setFilecontent(byte[] contenu){
		this.contenu = contenu;
	}
	/**
	 * <p>Nous allons lire le contenu du fichiers dont le path est nom. Si le fichier n'existe pas, on le créé.</p>
	 */
	public void setFromFile(String folder){
		Path path = Paths.get((folder.isEmpty()?"":folder+File.separator)+nom);
		try {
			if (!Files.exists(path)) {
				Files.createFile(path);
			}
			byte[] data = Files.readAllBytes(path);
			this.contenu = data;return;
		} catch (IOException e) { e.printStackTrace(); }
		this.contenu = null;
	}
	/**
	 * On écrit dans le fichier voulu le contenu du fichier que l'on a en mémoire.
	 */
	public void writeInFile(String folder){
		Path path = Paths.get((folder.isEmpty()?"":folder+File.separator)+nom);
		try {
			if (!Files.exists(path)) {
				System.out.println(path);
				Files.createFile(path);
			}
			if (this.contenu != null) {
				Files.write(path, this.contenu);
			}
		} catch (IOException e) { e.printStackTrace(); }
	}
}
