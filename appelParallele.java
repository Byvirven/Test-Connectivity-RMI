////////////////////////////////////////////////////////////
//                                                        //
// Auteur     : Dos Santos Oliveira Marco                 //
// Date       : 28 janvier 2012                           //
// Cours      : Systèmes distribués                       //
// Professeur : Nabil Abdennadher                         //
// Sujet      : Implémentation de l'algorithme            //
//              de Test Connectivity avec Java rmi        //
// Commentaire: Classe pour la gestion des threads        //
////////////////////////////////////////////////////////////

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class appelParallele extends Thread {
	// paramètres
	private int noeudAppele, noeudAppelant, noeudIntermediaire;
	private Interface ref_obj_distant;
	
	// constructeur
	public appelParallele(String lookup, int Destination,int Intermediaire,int Source) {
		noeudAppele = Destination;
		noeudAppelant = Source;
		noeudIntermediaire = Intermediaire;
		try {
			// Récupère la référence du voisin à contacter
			ref_obj_distant = (Interface) Naming.lookup(lookup);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	// méthode run
	public void run() {
		try {
			ref_obj_distant.envoyerNoeud(noeudAppele, noeudIntermediaire, noeudAppelant);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
}
