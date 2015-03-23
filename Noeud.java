////////////////////////////////////////////////////////////
//                                                        //
// Auteur     : Dos Santos Oliveira Marco                 //
// Date       : 9 janvier 2012                            //
// Cours      : Systèmes distribués                       //
// Professeur : Nabil Abdennadher                         //
// Sujet      : Implémentation de l'algorithme            //
//              de Test Connectivity avec Java rmi        //
// Commentaire: Classe pour la gestion des noeuds         //
////////////////////////////////////////////////////////////

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;
import java.util.*;

public class Noeud extends UnicastRemoteObject {
	// paramètres d'un noeud
	private int idI;
	private ArrayList<Integer> vI;
	private ArrayList<Boolean> atteintI;
	private ArrayList<Integer> countI;
	private ArrayList<Integer> parentI;
	private boolean initI;
	
	// constructeur du noeud
	public Noeud(int monId, int nbNoeud, ArrayList<Integer> mesVoisins, String Hote, String P, String Objet) throws RemoteException{
		System.out.println("L'objet distant "+Objet+String.valueOf(monId)+" est prêt sur le port "+String.valueOf(Integer.parseInt(P)+monId)+" du serveur "+Hote);
		idI = monId;
		vI = mesVoisins;
		atteintI = genererAtteint(nbNoeud);
		countI = genererCount(nbNoeud);
		parentI = genererParent(nbNoeud);
		initI = false;
	}
	
	// retourner l'identifiant du noeud
	public int getId() throws RemoteException {
		return idI;
	}
	
	// générer le tableau des noeuds atteint
	public ArrayList<Boolean> genererAtteint (int nbNoeud) throws RemoteException {
		ArrayList<Boolean> l = new ArrayList<Boolean>(nbNoeud);
		for (int i=0;i<nbNoeud;i++) {
			l.add(false);
		}
		return l;
	}
	
	// afficher les noeuds atteint
	public void afficherAtteint () throws RemoteException {
		System.out.println("Noeud atteint depuis le noeud"+String.valueOf(idI));
		System.out.println(String.valueOf(atteintI));
		
		
	}
	
	// générer le tableau du nombres de messages reçus
	public ArrayList<Integer> genererCount (int nbNoeud) throws RemoteException {
		ArrayList<Integer> l = new ArrayList<Integer>(nbNoeud);
		for (int i=0;i<nbNoeud;i++) {
			l.add(0);
		}
		return l;
	}
	
	// afficher le nombre de messages reçus
	public void afficherCount () throws RemoteException {
		System.out.println("Noeud compter depuis le noeud"+String.valueOf(idI));
		System.out.println(String.valueOf(countI));
	}
	
	// générer le tableau des parents
	public ArrayList<Integer> genererParent (int nbNoeud) throws RemoteException {
		ArrayList<Integer> l = new ArrayList<Integer>(nbNoeud);
		for (int i=0;i<nbNoeud;i++) {
			l.add(-1);
		}
		return l;
	}
	
	// afficher le tableau des parents
	public void afficherParent () throws RemoteException {
		System.out.println("Pour atteindre le noeud k depuis le noeud "+String.valueOf(idI));
		System.out.println(String.valueOf(parentI));
	}
	
	// afficher l'état du noeud
	public void afficherInit () throws RemoteException {
		if (initI) {
			System.out.println("Le noeud "+String.valueOf(idI) + " est initialisé");
		} else {
			System.out.println("Le noeud "+String.valueOf(idI) + " n'est pas initialisé");
		}
	}
	// initialiser le noeud
	public void setInit() {
		initI = true;
	} 
	// état du noeud
	public boolean getInit() {
		return initI;
	} 
	
	// marquer le noeud id comme atteint
	public void setAtteint(int id) {
		atteintI.set(id,true);
	}
	
	// savoir si un noeud id est atteint 
	public boolean getAtteint(int id) {
		return atteintI.get(id);
	}
	
	// connaitre le nombre de voisins directs du noeud
	public int getTailleVoisinage() {
		return vI.size();
	}
	
	// connaître le voisin à une position donnée
	public int getVoisin(int position) {
		return vI.get(position);
	}
	
	// incrémenter le nombre de messages reçu depuis un noeud
	public void setCount(int id) {
		countI.set(id,countI.get(id)+1);
	}
	// renvoyer le nombre message reçu depuis un noeud
	public int getCount(int id) {
		return countI.get(id);
	}
	
	// définir le premier noeuds id à destination du noeud parent
	public void setParent(int parent, int id) {
		parentI.set(parent,id);
	}
	
	// renvoyer le parent
	public int getParent(int parent) {
		return parentI.get(parent);
	} 

}
