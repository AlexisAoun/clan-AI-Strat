package strats;

import java.util.ArrayList;

import javax.tools.Tool;

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
    private boolean debug = true;

    private Terrain[] plateau;
    private int myColor, myScore, opponentScore;
    private int[] colorScore, opponentMov, opponentVillages;
    private int bestIdDst, bestIdSrc;

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

        int[] output = new int[2];

        int bestIdSrc = 0, bestIdDst = 0;
        double bestResult = 0;

        // on parcourt l'ensemble des territoires
        for (int iSrc = 0; iSrc < sourcePossible.length; iSrc++) {
            int[] destinationPossible = Tools.getVoisinsDispo(plateau, sourcePossible[iSrc]);

            for (int iDst = 0; iDst < destinationPossible.length; iDst++) {

                int res = computeResult(sourcePossible[iSrc], destinationPossible[iDst]); // Resultat si il ya creation
                                                                                          // de villages
                // System.out.println(" res computeResult : "+res);
                if (res > bestResult) {
                    bestResult = res;
                    bestIdSrc = sourcePossible[iSrc];
                    bestIdDst = destinationPossible[iDst];
                }
            }
        }
        if (bestResult == 0) {
            bestResult = -1;
            for (int iSrc = 0; iSrc < sourcePossible.length; iSrc++) {
                int[] destinationPossible = Tools.getVoisinsDispo(plateau, sourcePossible[iSrc]);

                for (int iDst = 0; iDst < destinationPossible.length; iDst++) {
                    int res = computeResultDistance(sourcePossible[iSrc], destinationPossible[iDst]);
                    // System.out.println(" res computeResultDistance : "+res);
                    //System.out.println(" res = "+res+" bestResult = "+bestResult);
                    //System.out.println(" bestIdSrc = "+bestIdSrc+" bestIdDst = "+bestIdDst);
                    if (res > bestResult) {
                        bestResult = res;
                        bestIdSrc = sourcePossible[iSrc];
                        bestIdDst = destinationPossible[iDst];
                    }
                }
            }

        }
        this.bestIdDst = bestIdDst;
        this.bestIdSrc = bestIdSrc;

        //System.out.println(" src : " + bestIdSrc + " dst :" + bestIdDst);
        output = new int[] { bestIdSrc, bestIdDst };
        return output;
    }

    public int computeResult(int idSrc, int idDst) {
        int output = 0;

        int[] gain = Tools.evaluerGain(plateau, idSrc, idDst, ordre(Tools.listeVillagesCreesSi(plateau, idSrc)));

        int bestOfOthers = 0;
        for (int i = 0; i < gain.length; i++)
            if (i != myColor && gain[i] > bestOfOthers)
                bestOfOthers = gain[i];

        output = Tools.nbVillageCreeSi(plateau, idSrc) + gain[myColor] - bestOfOthers;

        return output;
    }

    public int computeResultDistance(int idSrc, int idDst) {

        int output = 0;
        //System.out.println("Calc1");
        int myColorDistBefore = computeDistanceTotEntreMemeCouleur(this.plateau, myColor);
        //System.out.println("Exiting from calc1");
        int[] couleurPresente = plateau[idSrc].getCabanes();
        // System.out.println("Initializing1");
        int[] distCouleurPresenteBefore = new int[couleurPresente.length];
        // System.out.println("Initializing2");
        for (int i = 0; i < couleurPresente.length; i++) {

            //System.out.println("Loop couleurPresente taille = " + couleurPresente.length);

            if (couleurPresente[i] == 0 || i == myColor)
                distCouleurPresenteBefore[i] = 0;
            else
             //   System.out.println("Calc2 i = " + i);
            distCouleurPresenteBefore[i] = computeDistanceTotEntreMemeCouleur(this.plateau, couleurPresente[i]);
            //System.out.println("Exiting Calc2");
        }

        // System.out.println("Entering after phase");
        Terrain[] plateauSimule = simulerMove(this.plateau, idSrc, idDst);
        int sommeDistCouleurPrensente = 0;

        //System.out.println("Calc3");
        int myColorDistAfter = computeDistanceTotEntreMemeCouleur(plateauSimule, myColor);
        //System.out.println("Exiting Calc3");
        int[] distCouleurPresenteAfter = new int[couleurPresente.length];
        for (int i = 0; i < couleurPresente.length; i++) {
            if (couleurPresente[i] == 0 || i == myColor)
                distCouleurPresenteAfter[i] = 0;
            else
                distCouleurPresenteAfter[i] = computeDistanceTotEntreMemeCouleur(plateauSimule, couleurPresente[i]);
            sommeDistCouleurPrensente += distCouleurPresenteAfter[i];
        }

        // System.out.println("Calculating...");
        if (myColorDistAfter - myColorDistBefore < 0)
            output = -1000;
        else
            output = ((myColorDistAfter - myColorDistBefore) * 5) - sommeDistCouleurPrensente;

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
            if (centre.getCabanes(couleur) > 0 ) {
                //System.out.println("Found first color/center");
                for (int j = 0; j < wasCenter.length; j++)
                   wasCenter[j] = false;

                nbCouleur++;
                ArrayList<Integer> lastLayer = new ArrayList();
                ArrayList<Integer> newLayer = new ArrayList();
                lastLayer.add(i);
                //System.out.println("loop 0 absolute center :"+i);
                boolean finished = false;
                while (!found || finished) {
                    //System.out.println("loop 1 last layer = "+lastLayer.size());
                    int compteurCentre = 0;
                    if(lastLayer.size() == 0)
                        finished = true;
                    while (compteurCentre < lastLayer.size()) {
                        //System.out.println("loop 2 : centre.getNbVoisins : "+centre.getNbVoisins()+ " territoir candidat centre : "+lastLayer.get(compteurCentre));
                        if (!wasCenter[lastLayer.get(compteurCentre)]) {
                            centre = plateau[lastLayer.get(compteurCentre)];
                            wasCenter[lastLayer.get(compteurCentre)] = true;
                            int compteurVoisin = 0;
                            while(compteurVoisin < centre.getNbVoisins()) {
                                int indiceVoisin = centre.getVoisin(compteurVoisin);
                                //System.out.println("loop 3 : centreNbVoisins : "+centre.getNbVoisins()+ " indiceVoisin"+indiceVoisin );
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
                        //System.out.println("new layer : "+newLayer.size());
                        lastLayer.clear();
                        lastLayer = (ArrayList<Integer>)newLayer.clone();
                        newLayer.clear();
                    }
                }
            }
            distTot += dist;

        }

        //System.out.println("Debug compute distance fun | distTot = "+distTot+ " nbCouleur = "+nbCouleur);
        output = distTot;
        //System.out.println("Passed last calc");
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
        /*
         * int bestGain = 0; int[] output = new int[_villages.length];
         * 
         * for (int i = 0; i < _villages.length; i++) { int[] gain = new int[5]; int[]
         * newOrdre = new int[_villages.length]; newOrdre[0] = _villages[i]; int gainTot
         * = 0;
         * 
         * for (int j = 1; j < _villages.length; j++) { if (j != i) newOrdre[j] =
         * _villages[j]; } gain = Tools.evaluerGain(this.plateau, this.bestIdSrc,
         * this.bestIdDst, newOrdre); int bestOfOthers = 0; for (int j = 0; j <
         * gain.length; j++) if (j != myColor && gain[j] > bestOfOthers) bestOfOthers =
         * gain[j];
         * 
         * gainTot = Tools.nbVillageCreeSi(plateau, this.bestIdSrc) + gain[myColor] -
         * bestOfOthers;
         * 
         * if(gainTot>bestGain){ bestGain = gainTot; output = newOrdre; } }
         * 
         * return output;
         */
        return _villages;
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
