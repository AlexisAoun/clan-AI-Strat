package strats;

import java.util.ArrayList;

import javax.print.attribute.standard.MediaSize.ISO;

import clans.Terrain;

/**
 * Note (Anthony Fleury) : 
 * Cette strategie est a modifier et a adapter pour que vous puissiez rendre la votre.
 * Pour la modifier, avec le bouton droit, faite un Refactor, Rename et donnez lui le nom de votre classe 
 * StratX avec X votre numero de rendu. 
 * Une fois fait, completez CORRECTEMENT les methodes getName et getGroupes.
 * Vous n'aurez ensuite qu'a completer le code de votre propre strategie, a vous de jouer !
 */

/**
 * @author Alexis AOUN
 */
public class Strat22 implements Strategie {

    private Terrain[] plateau;
    private int myColor, myScore, opponentScore;
    private int[] colorScore, opponentMov, opponentVillages;
    private int bestIdDst, bestIdSrc, actualIdSrc, actualIdDst;

    public Strat22() {
        super();
    }

    @Override
    public int[] mouvement(Terrain[] _plateau, int _myColor, int[] _colorScore, int _myScore, int _opponentScore,
            int[] _opponentMov, int[] _opponentVillages) {

        // initialisation des variables
        plateau = _plateau;
        myColor = _myColor;
        myScore = _myScore;
        opponentScore = _opponentScore;
        colorScore = _colorScore;
        opponentMov = _opponentMov;
        opponentVillages = _opponentVillages;

        int[] sourcePossible = Tools.getSource(plateau);
        // System.out.println(" nbre de possibilte pour src : " +
        // sourcePossible.length);

        int[] output = new int[2];

        int bestIdSrc = 0, bestIdDst = 0;
        double bestResult = 0;

        // on parcourt l'ensemble des territoires
        for (int iSrc = 0; iSrc < sourcePossible.length; iSrc++) {
            int[] destinationPossible = Tools.getVoisinsDispo(plateau, sourcePossible[iSrc]);

            for (int iDst = 0; iDst < destinationPossible.length; iDst++) {

                this.actualIdSrc = sourcePossible[iSrc];
                this.actualIdDst = destinationPossible[iDst];

                int res = computeResult(actualIdSrc, actualIdDst); // Resultat si il ya creation
                                                                   // de villages
                // System.out.println(" res computeResult : " + res);
                if (res > bestResult) {
                    bestResult = res;
                    bestIdSrc = sourcePossible[iSrc];
                    bestIdDst = destinationPossible[iDst];
                }
            }
        }

        if (bestResult == 0 && sourcePossible.length < 10) {
            // System.out.println("New Strat");
            for (int iSrc = 0; iSrc < sourcePossible.length; iSrc++) {
                this.actualIdSrc = sourcePossible[iSrc];
                int[] prochainCoup = formationVillageProchainCoup(this.actualIdSrc);
                // System.out.println("Prochain coup : "+prochainCoup[0]+ " -
                // "+prochainCoup[1]);
                if (prochainCoup[0] == 1) {
                    bestIdSrc = this.actualIdSrc;
                    bestIdDst = prochainCoup[1];
                }
            }
        }
        if (bestIdSrc == 0 && bestIdDst == 0) {
            // System.out.println("Dist Strat");
            bestResult = -100;
            for (int iSrc = 0; iSrc < sourcePossible.length; iSrc++) {
                int[] destinationPossible = Tools.getVoisinsDispo(plateau, sourcePossible[iSrc]);

                for (int iDst = 0; iDst < destinationPossible.length; iDst++) {

                    this.actualIdSrc = sourcePossible[iSrc];
                    this.actualIdDst = destinationPossible[iDst];

                    int res = computeResultDistance(actualIdSrc, actualIdDst);
                    // System.out.println(" res computeResultDistance : " + res);
                    //System.out.println(" res = " + res + " bestResult = " + bestResult);
                    //System.out.println(" bestIdSrc = " + bestIdSrc + " bestIdDst = " + bestIdDst);
                    if (res > bestResult) {
                        bestResult = res;
                        bestIdSrc = sourcePossible[iSrc];
                        bestIdDst = destinationPossible[iDst];
                    }
                }
            }

        }

        this.actualIdDst = bestIdDst;
        this.actualIdSrc = bestIdSrc;

        this.bestIdDst = bestIdDst;
        this.bestIdSrc = bestIdSrc;

        //System.out.println(" src : " + bestIdSrc + " dst :" + bestIdDst);
        output = new int[] { bestIdSrc, bestIdDst };
        return output;
    }

    public int[] formationVillageProchainCoup(int idSrc) {
        int[] output = { 0, 0 };
        int[] voisinsDispo = Tools.getVoisinsDispo(this.plateau, idSrc);

        for (int i = 0; i < voisinsDispo.length; ++i) {
            int voisin = voisinsDispo[i];
            if (Tools.getNbVoisinsNonVide(this.plateau, voisin) < 3) {
                output[0] = 1;
                output[1] = voisin;
            }
        }
        return output;
    }

    public int computeResult(int idSrc, int idDst) {
        int output = 0;
        if (Tools.nbVillageCreeSi(this.plateau, idSrc) > 0) {

            int[] gain = Tools.evaluerGain(this.plateau, idSrc, idDst,
                    ordre(Tools.listeVillagesCreesSi(this.plateau, idSrc)));

            int bestOfOthers = 0;
            for (int i = 0; i < gain.length; i++)
                if (i != myColor && gain[i] > bestOfOthers)
                    bestOfOthers = gain[i];

            output = Tools.nbVillageCreeSi(this.plateau, idSrc) + gain[myColor] - bestOfOthers;
        }

        return output;
    }

    public int computeResultDistance(int idSrc, int idDst) {

        int output = 0;
        // System.out.println("Calc1");
        int myColorDistBefore = computeDistanceTotEntreMemeCouleur(this.plateau, myColor);
        // System.out.println("Exiting from calc1");
        int[] couleurPresente = plateau[idSrc].getCabanes().clone();
        // System.out.print("couleurPresente 1 : ");
        // afficheTabInt(couleurPresente);
        // System.out.println("Initializing1");
        int[] distCouleurPresenteBefore = new int[couleurPresente.length];
        int sommeDistCouleurPrensenteBefore = 0;
        // System.out.println("Initializing2");
        for (int i = 0; i < couleurPresente.length; i++) {

            // System.out.println("Loop couleurPresente taille = " +
            // couleurPresente.length);

            if (couleurPresente[i] == 0 || i == myColor)
                distCouleurPresenteBefore[i] = 0;
            else
                // System.out.println("Calc2 i = " + i);
                distCouleurPresenteBefore[i] = computeDistanceTotEntreMemeCouleur(this.plateau, couleurPresente[i]);
            // System.out.println("Exiting Calc2");
            sommeDistCouleurPrensenteBefore += distCouleurPresenteBefore[i];
        }

        // System.out.println("Entering after phase");
        Terrain[] plateauSimule = simulerMove(this.plateau, idSrc, idDst);
        int sommeDistCouleurPrensenteAfter = 0;
        // System.out.println("Calc3");
        int myColorDistAfter = computeDistanceTotEntreMemeCouleur(plateauSimule, myColor);
        // System.out.println("Exiting Calc3");
        // System.out.print("couleurPresente 2 : ");
        // afficheTabInt(couleurPresente);
        int[] distCouleurPresenteAfter = new int[couleurPresente.length];
        for (int i = 0; i < couleurPresente.length; i++) {
            if (couleurPresente[i] == 0 || i == myColor)
                distCouleurPresenteAfter[i] = 0;
            else {
                distCouleurPresenteAfter[i] = computeDistanceTotEntreMemeCouleur(plateauSimule, couleurPresente[i]);
            }
            // System.out.println(" distCouleurPresenteAfter of " + i + " = " +
            // distCouleurPresenteAfter[i]);
            sommeDistCouleurPrensenteAfter += distCouleurPresenteAfter[i];
        }

        // System.out.println(" myColorDistBefore = " + myColorDistBefore + "
        // myColorDistAfter = " + myColorDistAfter);
        // System.out.println(" sommeDistCouleurPresenteAfter = " +
        // sommeDistCouleurPrensenteAfter
        // + " sommeDistCouleurPresentreBefore = " + sommeDistCouleurPrensenteBefore);
        // System.out.println("Calculating...");
        if (myColorDistAfter - myColorDistBefore < 0)
            output = -1000;
        else
            output = ((myColorDistAfter - myColorDistBefore) * 5)
                    - (sommeDistCouleurPrensenteAfter - sommeDistCouleurPrensenteBefore);

        // System.out.println(" output of computeDistScore " + output);
        // System.out.println("Debug Distance : myColorDistBefore :
        // "+myColorDistBefore+" myColorDistAfter : "+myColorDistAfter);
        return output;
    }

    public int computeDistanceTotEntreMemeCouleur(Terrain[] plateau, int couleur) {
        int output = 0;
        double nbCouleur = 0;
        int distTot = 0;
        boolean[] checked = new boolean[plateau.length];
        boolean[] wasCenter = new boolean[plateau.length];

        for (int i = 0; i < checked.length; i++)
            checked[i] = false;

        for (int i = 0; i < wasCenter.length; i++)
            wasCenter[i] = false;

        for (int i = 0; i < plateau.length; i++) {
            int dist = 0;
            checked[i] = true;
            boolean found = false;
            Terrain centre = plateau[i];
            if (centre.getCabanes(couleur) > 0) {
                // System.out.println("Found first color/center");
                for (int j = 0; j < wasCenter.length; j++)
                    wasCenter[j] = false;

                nbCouleur++;
                ArrayList<Integer> lastLayer = new ArrayList();
                ArrayList<Integer> newLayer = new ArrayList();
                lastLayer.add(i);
                // System.out.println("loop 0 absolute center :"+i);
                boolean finished = false;
                while (!found || finished) {
                    // System.out.println("loop 1 last layer = "+lastLayer.size());
                    int compteurCentre = 0;
                    if (lastLayer.size() == 0)
                        finished = true;
                    while (compteurCentre < lastLayer.size()) {
                        // System.out.println("loop 2 : centre.getNbVoisins : "+centre.getNbVoisins()+ "
                        // territoir candidat centre : "+lastLayer.get(compteurCentre));
                        if (!wasCenter[lastLayer.get(compteurCentre)]) {
                            centre = plateau[lastLayer.get(compteurCentre)];
                            wasCenter[lastLayer.get(compteurCentre)] = true;
                            int compteurVoisin = 0;
                            while (compteurVoisin < centre.getNbVoisins()) {
                                int indiceVoisin = centre.getVoisin(compteurVoisin);
                                // System.out.println("loop 3 : centreNbVoisins : "+centre.getNbVoisins()+ "
                                // indiceVoisin"+indiceVoisin );
                                newLayer.add(compteurVoisin, indiceVoisin);
                                if (plateau[indiceVoisin].getCabanes(couleur) > 0) {
                                    if (!checked[indiceVoisin])
                                        nbCouleur++;
                                    checked[indiceVoisin] = true;
                                    found = true;
                                } else if (!checked[indiceVoisin])
                                    checked[indiceVoisin] = true;

                                compteurVoisin++;

                            }
                        }
                        compteurCentre++;
                    }
                    if (!found) {
                        dist++;
                        // System.out.println("new layer : "+newLayer.size());
                        lastLayer.clear();
                        lastLayer = (ArrayList<Integer>) newLayer.clone();
                        newLayer.clear();
                    }
                }
            }
            distTot += dist;

        }

        // System.out.println("Debug compute distance fun | distTot = "+distTot+ "
        // nbCouleur = "+nbCouleur);
        output = distTot;
        // System.out.println("Passed last calc");
        return output;
    }

    public Terrain[] simulerMove(Terrain[] plateau, int idScr, int idDst) {
        Terrain[] output = plateau;
        for (int i = 0; i < 5; i++)
            output[idDst].addCabane(i, output[idScr].getCabanes(i));
        output[idScr].vider();
        return output;
    }

    @Override
    public int[] ordre(int[] _villages) {
        int n = _villages.length;
        // System.out.println(" taille _villages = " + n);
        int[] combinaison = _villages;
        int[] indexes = new int[n];
        int[] output = new int[n];
        int gain = 0;
        for (int i = 0; i < n; i++) {
            indexes[i] = 0;
        }
        int i = 0;

        output = combinaison.clone();

        if (n > 1) {

            int gainMax = computeResultChoix(combinaison);

            // System.out.print("Tab input : ");
            // afficheTabInt(combinaison);

            while (i < n) {
                if (indexes[i] < i) {
                    combinaison = swap(combinaison, i % 2 == 0 ? 0 : indexes[i], i);
                    gain = computeResultChoix(combinaison);
                    indexes[i]++;
                    i = 0;
                } else {
                    indexes[i] = 0;
                    i++;
                }
                // System.out.println(" gain = " + gain + " gainMax = " + gainMax);
                // System.out.print(" output = ");
                // afficheTabInt(output);
                if (gain > gainMax) {
                    gainMax = gain;
                    // System.out.print(" Combinaison of new max = ");
                    // afficheTabInt(combinaison);
                    output = combinaison.clone();
                }
            }

        }

        // System.out.print("Tab output : ");
        // afficheTabInt(output);
        return output;
    }

    private int[] swap(int[] tab, int a, int b) {
        int tmp = tab[a];
        tab[a] = tab[b];
        tab[b] = tmp;

        return tab;
    }

    public int computeResultChoix(int[] ordre) {
        int output = 0;
        // System.out.println(" n ordre = "+ordre.length+" nbVilageCree =
        // "+Tools.nbVillageCreeSi(this.plateau, this.actualIdSrc));
        // afficheTabInt(ordre);

        int[] gain = Tools.evaluerGain(plateau, this.actualIdSrc, this.actualIdDst, ordre);

        int bestOfOthers = 0;
        for (int i = 0; i < gain.length; i++)
            if (i != myColor && gain[i] > bestOfOthers)
                bestOfOthers = gain[i];

        output = Tools.nbVillageCreeSi(plateau, this.actualIdSrc) + gain[myColor] - bestOfOthers;

        return output;
    }

    public void afficheTabInt(int[] tab) {
        for (int i = 0; i < tab.length; i++)
            System.out.print(tab[i] + " | ");

        System.out.println("");
    }

    /**
     * Remplissez cette m�thode avec le bon format expliqu� ci-dessous
     * 
     * @return le nom des �l�ves (sous le format NOM1_NOM2) NOM1 et NOM2 sont
     *         uniquement les noms de famille de votre binome
     */
    public String getName() {
        return "AOUN";
    }

    public String getGroupe() {
        return "3";
    }

}
