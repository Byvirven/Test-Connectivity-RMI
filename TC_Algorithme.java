////////////////////////////////////////////////////////////
//                                                        //
// Auteur     : Dos Santos Oliveira Marco                 //
// Date       : 9 janvier 2012                            //
// Cours      : Systèmes distribués                       //
// Professeur : Nabil Abdennadher                         //
// Sujet      : Implémentation de l'algorithme            //
//              de Test Connectivity avec Java rmi        //
//                                                        //
////////////////////////////////////////////////////////////

import java.rmi.*;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;
import java.util.*;

public class TC_Algorithme extends UnicastRemoteObject implements Interface {

	// paramètres génériques
	private static String Machine;
	private static String Port;
	private static String Nom;
	private static ArrayList<ArrayList<Integer>> Voisins;
	
	// structure pour les noeud et registre
	private static ArrayList<Noeud> Noeud;
	private static ArrayList<Registry> Registre;
	
	// compteur pour les threads
	private static int cptThread = 0;
	// compteur message total
	private static int cptMsg = 0;
	
	public TC_Algorithme(int monId, int nbNoeud, ArrayList<Integer> mesVoisins) throws RemoteException {
		Noeud.add(new Noeud(monId,nbNoeud,mesVoisins, Machine, Port, Nom));
	}
	
	public synchronized void envoyerNoeud (int Destination,int Intermediaire,int Source) throws RemoteException {
		try {
			// petit message
			System.out.println("Message du noeud "+Nom+String.valueOf(Source)+" sur le port "+String.valueOf(Integer.parseInt(Port)+Destination)+" du serveur "+Machine+" à destination du noeud " + Nom+String.valueOf(Destination));
			cptMsg++;
			
			// si non init
			if (!Noeud.get(Destination).getInit() ) {
				// init = true et atteint(i) = true
				Noeud.get(Destination).setInit();
				Noeud.get(Destination).setAtteint(Destination);
				// envoyer id(i) à tous les voisin de ni
				for (int i=0;i< Noeud.get(Destination).getTailleVoisinage();i++) {
					// incrémenter le compteur et lancer les threads
					incThread();
					new appelParallele("//"+Machine+":"+String.valueOf(Integer.parseInt(Port)+Noeud.get(Destination).getVoisin(i))+"/"+Nom+String.valueOf(Noeud.get(Destination).getVoisin(i)), Noeud.get(Destination).getVoisin(i),Noeud.get(Destination).getId(),Noeud.get(Destination).getId()).start();
				}
			}
			
			// count(k) +=1
				Noeud.get(Destination).setCount(Source);
				// si non atteint(k)
				if(!Noeud.get(Destination).getAtteint(Source)) {
					Noeud.get(Destination).setAtteint(Source);
					Noeud.get(Destination).setParent(Source,Intermediaire);
					// envoyer id(k) à tous les voisins de ni != nj
					for (int i=0;i< Noeud.get(Destination).getTailleVoisinage();i++) {
						if (Noeud.get(Destination).getVoisin(i) != Intermediaire) {
							// incrémenter et lancer les threads
							incThread();
							new appelParallele("//"+Machine+":"+String.valueOf(Integer.parseInt(Port)+Noeud.get(Destination).getVoisin(i))+"/"+Nom+String.valueOf(Noeud.get(Destination).getVoisin(i)), Noeud.get(Destination).getVoisin(i),Noeud.get(Destination).getId(),Source).start();
						}
					}
				}
			// si count(k) = |vi| et parent(k) != -1
			// (-1 au lieu de null ici pour éviter les problèmes de type)
			if (Noeud.get(Destination).getCount(Source) == Noeud.get(Destination).getTailleVoisinage() && Noeud.get(Destination).getParent(Source) != -1) {
				// incrémenter le compteur et lancer le dernier thread
				incThread();
				new appelParallele("//"+Machine+":"+String.valueOf(Integer.parseInt(Port)+Noeud.get(Destination).getParent(Source))+"/"+Nom+String.valueOf(Noeud.get(Destination).getParent(Source)),Noeud.get(Destination).getParent(Source),Noeud.get(Destination).getId(),Source).start();
			}
			// décrémenter les threads
			decThread();
		} catch (RemoteException e) {
			System.out.println("Le serveur est en panne : "+e.getMessage());
		}
	}
	
	// incrémenter le compteur de thread en cours...
	public static synchronized void incThread() {
		cptThread++;
	}
	// décrémenter le compteur de thread
	public static synchronized void decThread() {
		cptThread--;
	}
	
	// générer aléatoirement les noeuds et leurs arêtes
	public static ArrayList<ArrayList<Integer>> genererVoisin (int nbNoeud) throws Exception {
		// on crée un ArrayList de taille nbNoeud
		ArrayList<ArrayList<Integer>> l = new ArrayList<ArrayList<Integer>>(nbNoeud); 
		// préparer le générateur de nombre aléatoire
		Random  randomGenerator = new Random();
		// pour chaque noeud
		for (int i=0;i<nbNoeud;i++) {
			// on tire aléatoirement le nombre de voisins
			// auxquels sera relié le noeud i
			int nbVoisin = randomGenerator.nextInt(nbNoeud-1)+1;
			// on crée une nouvelle liste de taille nbVoisin
			l.add(i, new ArrayList<Integer>(nbVoisin));
			// à partir du deuxième tour, on recopie
			// le voisins des tours précédents si nécessaire...
			if (i>0) {
				for(int j=0;j<i;j++) {
					// vérifier la présence du noeud
					if (l.get(j).contains(i)) {
						l.get(i).add(j);
					}
				}
			}
			
			// initialisation du compteur pour éviter les boucles
			// infinis dans la génération des voisins 
			int cpt = 0;
			while (l.get(i).size() < nbVoisin) {
				// on tire aléatoirement le numéro du voisin
				int numVoisin = randomGenerator.nextInt(nbNoeud);
				// on s'assure que le voisin est supérieure au noeud
				// lui-même et qu'il n'est pas déjà présent 
				if (numVoisin > i && !l.get(i).contains(numVoisin)) {
					l.get(i).add(numVoisin);
				}
				// pour éviter les boucles infinis quand plus aucun
				// voisin n'est disponible, on décrémente le nombre
				// de voisins auxquels est connecté le noeud.
				cpt++;
				if (cpt > (nbNoeud*nbNoeud)-1) nbVoisin--;
			}
		}
		// afficher la liste des connexions créées pour debug
		/*for(int i=0;i<l.size();i++) {
			System.out.println("le noeud "+String.valueOf(i)+" a pour voisin le(s) noeud(s) :");
			for(int j=0;j<l.get(i).size();j++) {
				System.out.println(String.valueOf(l.get(i).get(j)));
			}
		}*/
		return l;
	}
	
	public static void main(String[] args)  throws IOException { 
		try {
			// créer un terminal d'écriture
			Console c = System.console();
			if (c == null) {
				System.err.println("Pas de terminal.");
				System.exit(1);
			}
			// nom de la machine
			// ne fonctionne qu'avec 127.0.0.1 ou localhost.localdomain sur ma machine'
			Machine = c.readLine("\nNom de la machine (en général 127.0.0.1 ou localhost.localdomain): ");
			if (Machine.isEmpty()) {
				Machine = "localhost.localdomain";
			}
			
			
			// choix du port
			// par défaut le 1099
			Port = c.readLine("\nPort de la machine (par défaut 1099): ");
			// vérifier si le port est vide 
			// ou si il ne s'agit pas d'une valeur numérique entière
			// ou si le port est plus petit que 1099
			if (Port.isEmpty() || 
				!Port.matches("[0-9]+") || 
				Integer.parseInt(Port) < 1099) {
				Port = "1099";
			}
			
			// nom du service
			Nom = c.readLine("Nom de l'objet distant' (par défaut Noeud): ");
			// vérifier si le nom est vide
			if (Nom.isEmpty()) {
				Nom = "Noeud";
			}
			
			// choix du mode : fixe ou aléatoire
			char type = 'u';
			String nbNoeud;
			while (type != '0' && type != '1') {
				System.out.println("Souhaitez vous générer un graphe aléatoire ou charger le graphe hardcodé ? \n(0 = aléatoire, 1 = hardcodé)\n");
				type = (char)System.in.read();
			}
			if (type=='0') {
				// nombre de noeud du graphe
				nbNoeud = c.readLine("Combien de noeuds possède le graphe (par défaut minimum 2): ");
				// vérifier si le nombre de noeud est vide
				// ou si il ne s'agit pas d'une valeur numérique entière
				// ou si le nombre de noeud est inférieur à 2
				if (nbNoeud.isEmpty() || 
					!nbNoeud.matches("[0-9]+") || 
					Integer.parseInt(nbNoeud) < 2) {
						nbNoeud = "2";
				}
			
				// génération de la liste des voisins aléatoirement
				Voisins = genererVoisin(Integer.parseInt(nbNoeud));
			} else {
				// nombre de noeud du graphe hardcodé
				nbNoeud = "7";
				// instancier la liste des noeuds
				Voisins = new ArrayList<ArrayList<Integer>>(Integer.parseInt(nbNoeud));
				// définir les noeuds et leurs arêtes
				Voisins.add(0, new ArrayList<Integer>(3));
				Voisins.get(0).add(2);
				Voisins.get(0).add(5);
				Voisins.get(0).add(6);
				Voisins.add(1, new ArrayList<Integer>(2));
				Voisins.get(1).add(2);
				Voisins.get(1).add(3);
				Voisins.add(2, new ArrayList<Integer>(4));
				Voisins.get(2).add(0);
				Voisins.get(2).add(1);
				Voisins.get(2).add(4);
				Voisins.get(2).add(5);
				Voisins.add(3, new ArrayList<Integer>(2));
				Voisins.get(3).add(4);
				Voisins.get(3).add(1);
				Voisins.add(4, new ArrayList<Integer>(2));
				Voisins.get(4).add(2);
				Voisins.get(4).add(3);
				Voisins.add(5, new ArrayList<Integer>(2));
				Voisins.get(5).add(0);
				Voisins.get(5).add(2);
				Voisins.add(6, new ArrayList<Integer>(1));
				Voisins.get(6).add(0);
			}
			
			// instancier les registre et les objets (noeud) rmi
			Noeud = new ArrayList<Noeud>(Integer.parseInt(nbNoeud));
			Registre = new ArrayList<Registry>(Integer.parseInt(nbNoeud));
			for (int i=0; i<Integer.parseInt(nbNoeud);i++) {
				// commande rmiregistry <Port> &
				Registre.add(LocateRegistry.createRegistry(Integer.parseInt(Port)+i));
				// instanciation de l'objet distant
				Naming.bind("//"+Machine+":"+String.valueOf(Integer.parseInt(Port)+i)+"/"+Nom+String.valueOf(i), new TC_Algorithme (i,Integer.parseInt(nbNoeud), Voisins.get(i) ) );
			}
			
			// on démarre seulement un noeud pour faire simple
			// mais on peut augmenter jusqu'à nbNoeud-1 maximum
			for (int i = 0; i<1;i++) {
				// ni appartient à N0
				Noeud.get(i).setInit();
				Noeud.get(i).setAtteint(i);
				for (int j=0;j< Noeud.get(i).getTailleVoisinage();j++) {
					// incrémenter le compteur de threads
					incThread();
					// lancer thread
					new appelParallele("//"+Machine+":"+String.valueOf(Integer.parseInt(Port)+Noeud.get(i).getVoisin(j))+"/"+Nom+String.valueOf(Noeud.get(i).getVoisin(j)), Noeud.get(i).getVoisin(j),i,i).start();
				}
			}
			
			// histoire d'attendre la fin de tous les thread...
			while (cptThread != 0) { Thread.sleep(100); }
			
			// afficher les résultats
			for (int i = 0; i<Integer.parseInt(nbNoeud);i++) {
				Noeud.get(i).afficherInit();
				Noeud.get(i).afficherAtteint();
				Noeud.get(i).afficherParent();
				Noeud.get(i).afficherCount();
				
			}
			// afficher les noeuds et leurs voisins
			System.out.println("Listes des Noeuds et leurs voisins :");
			System.out.println(String.valueOf(Voisins));
			// nombre de messages échangés
			System.out.println(String.valueOf(cptMsg)+" messages ont été échangé.");
			
			// fin
			System.out.println("Fin de programme.");
			System.exit(0);
			// problème de connexion
		} catch (Exception e) {
			System.out.println("Le serveur est en panne : "+e.getMessage());
		}
	}
}

