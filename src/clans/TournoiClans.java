package clans;

import strats.*;

/**
 *
 * @author Anthony Fleury
 */
public class TournoiClans {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        // Lance une partie entre deux strategies pour lesquelles vous donnez les numeros
        //combatSimple(0, 2);
        
        // lance un nombre defini de parties (troisieme parametres) entre deux strategies dont vous donnez le numero (deux premiers parametres)
        combatMultiples(7, 9, 10000);

        // Tournoi a 10000 parties par duels avec les strategies du tableau suivant:
        // Strategies des enseignants : 1, 2, 6 et 8. Attention 8 est tres gourmande en calcul donc limiter le nombre de parties...
        int strategies[] = {1, 2, 3, 4, 6, 7, 8, 9};
        //lanceTournoi(strategies, 1000);

        // Permet de jouer contre une strategie donnee, avec l'interface graphique.
        //testStrat(0);
    }
    
    public static void lanceTournoi(int[] strats, int n) {
        int nMatchsGagnes[] = new int[strats.length];
        System.out.println("Tournoi :" );
        for (int i = 0; i < strats.length; i++) {
            for (int j = i+1; j < strats.length; j++) {
                int nVictoireIJ[] = {0,0};
                int nPointsIJ[] = {0,0};
                
                for (int k = 0; k < n; k++) {
                    Strategie s1 = null, s2 = null;

                    try {
                        s1 = (Strategie) Class.forName("strats.Strat" + strats[i]).newInstance();
                        s2 = (Strategie) Class.forName("strats.Strat" + strats[j]).newInstance();

                    } catch (Exception ex) {
                        System.out.println("Chargement impossible");
                        return;
                    }
                    if (k % 2 == 0) {
                        Joueur ent1 = new Joueur("Joueur 1", s1);
                        Joueur ent2 = new Joueur("Joueur 2", s2);

                        int[] t = lanceJeu(ent1, ent2);

                        nPointsIJ[0] += t[0];
                        nPointsIJ[1] += t[1];
                        nVictoireIJ[t[0] > t[1] ? 0 : 1]++;
                    } else {
                        Joueur ent1 = new Joueur("Joueur 2", s2);
                        Joueur ent2 = new Joueur("Joueur 1", s1);

                        int[] t = lanceJeu(ent1, ent2);

                        nPointsIJ[1] += t[0];
                        nPointsIJ[0] += t[1];
                        nVictoireIJ[t[0] > t[1] ? 1 : 0]++;
                    }
                }
                
                System.out.println("\tJoueur 1 (strat" + strats[i] + "): " + nVictoireIJ[0] + " victoires (" + nPointsIJ[0] + " points) - Joueur 2 (strat" + strats[j] + ") : " + nVictoireIJ[1] + " victoires (" + nPointsIJ[1] + " points). Strat " + (nVictoireIJ[0] > nVictoireIJ[1] ? strats[i] : strats[j]) + " gagne.");
                nMatchsGagnes[nVictoireIJ[0] > nVictoireIJ[1] ? i : j]++;
            }
        }
        
        for(int i = 0; i < strats.length; i++) {
            System.out.println("\tStrategie " + strats[i] + " a gagne " + nMatchsGagnes[i] + " matchs.");
        }
    }
    
    public static void testStrat(int numStrat) {
        Strategie s1 = null;
        
        try {
            s1 = (Strategie) Class.forName("strats.Strat"+numStrat).newInstance();
        } catch (Exception ex) {
            System.out.println("Chargement impossible");
            return;
        }
        
        Jeu j = new Jeu();
        
        Joueur ent1 = new Joueur("IA", s1);
        Joueur ent2 = new Joueur("Humain");        
        
        j.run(ent1, ent2, true, false, 5);
    }
   
    public static void combatSimple(int strat1, int strat2) {
        Strategie s1 = null, s2 = null;
        
        try {
            s1 = (Strategie) Class.forName("strats.Strat"+strat1).newInstance();
            s2 = (Strategie) Class.forName("strats.Strat"+strat2).newInstance();

        } catch (Exception ex) {
            System.out.println("Chargement impossible");
            return;
        }

        Joueur ent1 = new Joueur("Joueur 1", s1);
        Joueur ent2 = new Joueur("Joueur 2", s2);
        
        int[] score = lanceJeu(ent1, ent2);
        
        System.out.println("combat Simple :" );
        System.out.println("\tJoueur 1 (strat" + strat1 + "): " + score[0] + " points - Joueur 2 (strat" + strat2 + ") : " + score[1] + " points. Strat " + (score[0] > score[1] ? strat1 : strat2) + " gagne.");
      }
    
     public static void combatMultiples(int strat1, int strat2, int n) {
        int nVictoires[] = {0,0}, nPoints[] = {0,0};
        
        for(int i = 0; i < n; i++) {
             Strategie s1 = null, s2 = null;

             try {
                 s1 = (Strategie) Class.forName("strats.Strat" + strat1).newInstance();
                 s2 = (Strategie) Class.forName("strats.Strat" + strat2).newInstance();

             } catch (Exception ex) {
                 System.out.println("Chargement impossible");
                 return;
             }

             if (i % 2 == 0) {
                 Joueur ent1 = new Joueur("Joueur 1", s1);
                 Joueur ent2 = new Joueur("Joueur 2", s2);

                 int[] t = lanceJeu(ent1, ent2);

                 nPoints[0] += t[0];
                 nPoints[1] += t[1];
                 nVictoires[t[0] > t[1] ? 0 : 1]++;
             } else {
                 Joueur ent1 = new Joueur("Joueur 2", s2);
                 Joueur ent2 = new Joueur("Joueur 1", s1);

                 int[] t = lanceJeu(ent1, ent2);

                 nPoints[1] += t[0];
                 nPoints[0] += t[1];
                 nVictoires[t[0] > t[1] ? 1 : 0]++;
             }
         }

        System.out.println("combat Multiples :" );
        System.out.println("\tJoueur 1 (strat" + strat1 + "): " + nVictoires[0] + " victoires (" + nPoints[0] + " points) - Joueur 2 (strat" + strat2 + ") : " + nVictoires[1] + " victoires (" + nPoints[1] + " points). Strat " + (nVictoires[0] > nVictoires[1] ? strat1 : strat2) + " gagne.");        
    }
 
    public static int[] lanceJeu(Joueur _j1, Joueur _j2) {
        Jeu j = new Jeu();
      
        j.initPlateau();
        j.placementCabane();
        j.initJoueur(_j1, _j2);
        j.initColors();
        //j.toss();
        
        int tour = 1;

        while (!j.end()) {
            j.jouer();
            j.changementDeJoueur();
            tour++;
        }
        
        return new int[] {j.joueurs[0].getNbVillage() + j.score[j.joueurs[0].getCouleur()],
            j.joueurs[1].getNbVillage() + j.score[j.joueurs[1].getCouleur()]};
    }
}
