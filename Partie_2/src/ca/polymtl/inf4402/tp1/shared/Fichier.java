/**
 * 
 */
package ca.polymtl.inf4402.tp1.shared;

/**
 * @author Antoine
 *
 */
public class Fichier {
	private String nom;
	private Integer clientid;
	private byte[] contenu;
	
	public Fichier(String name){
		this.nom = name;
		this.clientid = 0;
		this.contenu = null;
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
}
