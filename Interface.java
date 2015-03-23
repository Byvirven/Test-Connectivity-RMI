////////////////////////////////////////////////////////////
//                                                        //
// Auteur     : Dos Santos Oliveira Marco                 //
// Date       : 9 janvier 2012                            //
// Cours      : Systèmes distribués                       //
// Professeur : Nabil Abdennadher                         //
// Sujet      : Implémentation de l'interface de l'algo-  //
//              rithme de Test Connectivity avec Java rmi //
//                                                        //
////////////////////////////////////////////////////////////

import java.rmi.*;
import java.util.*;

public interface Interface extends Remote {
	// methode distante
	public void envoyerNoeud (int Destination,int intermediaire,int Source) throws RemoteException;
}
