package strats;

import java.util.ArrayList;
import java.util.Random;


import clans.Terrain;

/**
 * @author Alexis AOUN
 */
public class Strat22 implements Strategie {

    private Terrain[] plateau;
    private int myColor;
    private int actualIdSrc, actualIdDst;

    public Strat22() {
        super();
    }

    /**
     * 
     * @param _plateau
     * @param _myColor
     * @param _colorScore
     * @param _myScore
     * @param _opponentScore
     * @param _opponentMov
     * @param _opponentVillages
     * @return 
     */
    @Override
    public int[] mouvement(Terrain[] _plateau, int _myColor, int[] _colorScore, int _myScore, int _opponentScore,
            int[] _opponentMov, int[] _opponentVillages) {

        // initialisation des variables
        plateau = _plateau;
        myColor = _myColor;
     ;

        Random r = new Random();

        int[] sourcePossible = Tools.getSource(plateau);

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
                if (res > bestResult) {
                    bestResult = res;
                    bestIdSrc = sourcePossible[iSrc];
                    bestIdDst = destinationPossible[iDst];
                }
            }
        }

        if (bestResult == 0 && sourcePossible.length < 45) {
            for (int iSrc = 0; iSrc < sourcePossible.length; iSrc++) {
                this.actualIdSrc = sourcePossible[iSrc];
                if (!formationVillageCoupDApres(this.actualIdSrc)){
                    bestIdSrc = this.actualIdSrc;
                    int[] destinationPossible = Tools.getVoisinsDispo(this.plateau, this.actualIdSrc);
                    bestIdDst = destinationPossible[r.nextInt(destinationPossible.length)];
                }
            }
        }
        if (bestIdSrc == 0 && bestIdDst == 0) {
            bestResult = -100;
            for (int iSrc = 0; iSrc < sourcePossible.length; iSrc++) {
                int[] destinationPossible = Tools.getVoisinsDispo(plateau, sourcePossible[iSrc]);

                for (int iDst = 0; iDst < destinationPossible.length; iDst++) {

                    this.actualIdSrc = sourcePossible[iSrc];
                    this.actualIdDst = destinationPossible[iDst];

                    int res = computeResultDistance(actualIdSrc, actualIdDst);
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


        output = new int[] { bestIdSrc, bestIdDst };
        return output;
    }
    
    
    /**
     * 
     * @param idSrc
     * @return 
     */
    public boolean formationVillageCoupDApres(int idSrc) {
        boolean output = false;
        int nbVoisins = this.plateau[idSrc].getNbVoisins();
        for (int i = 0; i < nbVoisins; ++i) {
            if (Tools.getNbVoisinsNonVide(this.plateau, this.plateau[idSrc].getVoisin(i)) < 3) {
                output = true;
            }
        }
        return output;
    }

    /**
     * 
     * @param idSrc
     * @param idDst
     * @return 
     */
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

    
    /**
     * 
     * @param idSrc
     * @param idDst
     * @return 
     */
    public int computeResultDistance(int idSrc, int idDst) {

        int output = 0;
        int myColorDistBefore = computeDistanceTotEntreMemeCouleur(this.plateau, myColor);
        int[] couleurPresente = plateau[idSrc].getCabanes().clone();
        int[] distCouleurPresenteBefore = new int[couleurPresente.length];
        int sommeDistCouleurPrensenteBefore = 0;
        for (int i = 0; i < couleurPresente.length; i++) {


            if (couleurPresente[i] == 0 || i == myColor)
                distCouleurPresenteBefore[i] = 0;
            else
                distCouleurPresenteBefore[i] = computeDistanceTotEntreMemeCouleur(this.plateau, couleurPresente[i]);
            sommeDistCouleurPrensenteBefore += distCouleurPresenteBefore[i];
        }

        Terrain[] plateauSimule = simulerMove(this.plateau, idSrc, idDst);
        int sommeDistCouleurPrensenteAfter = 0;
        int myColorDistAfter = computeDistanceTotEntreMemeCouleur(plateauSimule, myColor);
        int[] distCouleurPresenteAfter = new int[couleurPresente.length];
        for (int i = 0; i < couleurPresente.length; i++) {
            if (couleurPresente[i] == 0 || i == myColor)
                distCouleurPresenteAfter[i] = 0;
            else {
                distCouleurPresenteAfter[i] = computeDistanceTotEntreMemeCouleur(plateauSimule, couleurPresente[i]);
            }
            sommeDistCouleurPrensenteAfter += distCouleurPresenteAfter[i];
        }

        if (myColorDistAfter - myColorDistBefore < 0)
            output = -1000;
        else
            output = ((myColorDistAfter - myColorDistBefore) * 5)
                    - (sommeDistCouleurPrensenteAfter - sommeDistCouleurPrensenteBefore);

        return output;
    }

    /**
     * 
     * @param plateau
     * @param couleur
     * @return 
     */
    public int computeDistanceTotEntreMemeCouleur(Terrain[] plateau, int couleur) {
        int output = 0;
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
                for (int j = 0; j < wasCenter.length; j++)
                    wasCenter[j] = false;

                ArrayList<Integer> lastLayer = new ArrayList();
                ArrayList<Integer> newLayer = new ArrayList();
                lastLayer.add(i);
                boolean finished = false;
                while (!found || finished) {
                    int compteurCentre = 0;
                    if (lastLayer.size() == 0)
                        finished = true;
                    while (compteurCentre < lastLayer.size()) {
                        if (!wasCenter[lastLayer.get(compteurCentre)]) {
                            centre = plateau[lastLayer.get(compteurCentre)];
                            wasCenter[lastLayer.get(compteurCentre)] = true;
                            int compteurVoisin = 0;
                            while (compteurVoisin < centre.getNbVoisins()) {
                                int indiceVoisin = centre.getVoisin(compteurVoisin);
                                newLayer.add(compteurVoisin, indiceVoisin);
                                if (plateau[indiceVoisin].getCabanes(couleur) > 0) {
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
                        lastLayer.clear();
                        lastLayer = (ArrayList<Integer>) newLayer.clone();
                        newLayer.clear();
                    }
                }
            }
            distTot += dist;

        }

        output = distTot;
        return output;
    }

    /**
     * 
     * @param plateau
     * @param idScr
     * @param idDst
     * @return 
     */
    public Terrain[] simulerMove(Terrain[] plateau, int idScr, int idDst) {
        Terrain[] output = plateau;
        for (int i = 0; i < 5; i++)
            output[idDst].addCabane(i, output[idScr].getCabanes(i));
        output[idScr].vider();
        return output;
    }

    /**
     * 
     * @param _villages
     * @return 
     */
    @Override
    public int[] ordre(int[] _villages) {
        int n = _villages.length;
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
                if (gain > gainMax) {
                    gainMax = gain;
                    output = combinaison.clone();
                }
            }

        }

        return output;
    }

    /**
     * 
     * @param tab
     * @param a
     * @param b
     * @return 
     */
    private int[] swap(int[] tab, int a, int b) {
        int tmp = tab[a];
        tab[a] = tab[b];
        tab[b] = tmp;

        return tab;
    }

    /**
     * 
     * @param ordre
     * @return 
     */
    public int computeResultChoix(int[] ordre) {
        int output = 0;

        int[] gain = Tools.evaluerGain(plateau, this.actualIdSrc, this.actualIdDst, ordre);

        int bestOfOthers = 0;
        for (int i = 0; i < gain.length; i++)
            if (i != myColor && gain[i] > bestOfOthers)
                bestOfOthers = gain[i];

        output = Tools.nbVillageCreeSi(plateau, this.actualIdSrc) + gain[myColor] - bestOfOthers;

        return output;
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
