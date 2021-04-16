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
     * Fonction déterminant le mouvement de l'IA
     * 
     * @param _plateau          l'état actuel du plateau de jeu
     * @param _myColor          la couleur de notre IA
     * @param _colorScore       le score des différentes couleurs
     * @param _myScore          le score de notre IA
     * @param _opponentScore    le score du joueur adverse
     * @param _opponentMov      le mouvement du joueur adverse au tour précédent
     * @param _opponentVillages les villages formé par le joueur au tour précédent
     * @return un tableau de deux entiers, le premier indiquant le territoire source
     *         et le deuxième le territoire destination
     */
    @Override
    public int[] mouvement(Terrain[] _plateau, int _myColor, int[] _colorScore, int _myScore, int _opponentScore,
            int[] _opponentMov, int[] _opponentVillages) {

        // initialisation des variables
        plateau = _plateau;
        myColor = _myColor;

        Random r = new Random();

        int[] sourcePossible = Tools.getSource(plateau);

        int[] output = new int[2];

        int bestIdSrc = 0, bestIdDst = 0;
        double bestResult = 0;

        // on étudie le cas où il y a création de villages
        // on parcourt l'ensemble des territoires
        // afin de trouver la combinaison rapportant le score le plus élevé
        for (int iSrc = 0; iSrc < sourcePossible.length; iSrc++) {
            int[] destinationPossible = Tools.getVoisinsDispo(plateau, sourcePossible[iSrc]);

            for (int iDst = 0; iDst < destinationPossible.length; iDst++) {

                this.actualIdSrc = sourcePossible[iSrc];
                this.actualIdDst = destinationPossible[iDst];

                // On caclule le score pour chaque combinaison
                int res = computeResult(actualIdSrc, actualIdDst);

                // et on sauvegarde le meilleur score
                if (res > bestResult) {
                    bestResult = res;
                    bestIdSrc = sourcePossible[iSrc];
                    bestIdDst = destinationPossible[iDst];
                }
            }
        }

        // Si il n'ya pas de villages créés et que il y'a moins de 45 territoires
        // sources disponibles
        // On prend le premier territoire source ne pouvant pas entrainer la formation
        // de village au prochain coup
        // Et on prend une destination aléatoire entre les voisins dispo de ce
        // territoire source
        if (bestResult == 0 && sourcePossible.length < 45) {
            for (int iSrc = 0; iSrc < sourcePossible.length; iSrc++) {
                this.actualIdSrc = sourcePossible[iSrc];
                if (!formationVillageProchainCoup(this.actualIdSrc)) {
                    bestIdSrc = this.actualIdSrc;
                    int[] destinationPossible = Tools.getVoisinsDispo(this.plateau, this.actualIdSrc);
                    bestIdDst = destinationPossible[r.nextInt(destinationPossible.length)];
                }
            }
        }

        // Si on le nombre de territoires sources possibles est plus grand que 45 ou que
        // l'on n'a pas trouvé
        // de territoire source qui n'engendre pas de formation de village au prochain
        // coup
        // on calcule un score se basant sur l'espacement des pions de couleur de l'IA
        // et des autres couleurs
        // le score le plus grand, qui signifie un accroissement de l'espacement des
        // pions de couleur de l'IA
        // et/ou une diminution de l'espacement des autres couleurs, et sauvegardé
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
     * @param idSrc le territoire source actuel
     * @return vrai si un village peut etre formé au prochain coup, faux sinon
     */
    public boolean formationVillageProchainCoup(int idSrc) {
        boolean output = false;
        int nbVoisins = this.plateau[idSrc].getNbVoisins();

        for (int i = 0; i < nbVoisins; ++i) {
            if (Tools.getNbVoisinsNonVide(this.plateau, this.plateau[idSrc].getVoisin(i)) < 3)
                output = true;
        }

        return output;
    }

    /**
     * 
     * @param idSrc le territoire source actuel
     * @param idDst le territoire destination actuel
     * @return Si des villages sont créés, retourne un score qui est la différence
     *         entre le gain du joueur+sa couleur et le meilleur gain des autres
     *         couleurs
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
     * @param idSrc le territoire source actuel
     * @param idDst le territoire destination actuel
     * @return retourne un score qui représente la différence entre l'évolution de
     *         l'espacement des pions de couleur de celui de l'IA et l'évolution de
     *         l'espacement des autres couleurs
     */
    public int computeResultDistance(int idSrc, int idDst) {

        int output = 0;

        // On calcule les distances entre les couleurs avant le mouvement
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

        // On calcule les distances après les couleurs apres le mouvement
        Terrain[] plateauSimule = simulerMove(this.plateau, idSrc, idDst);
        int sommeDistCouleurPrensenteAfter = 0;
        int myColorDistAfter = computeDistanceTotEntreMemeCouleur(plateauSimule, myColor);
        int[] distCouleurPresenteAfter = new int[couleurPresente.length];

        for (int i = 0; i < couleurPresente.length; i++) {

            if (couleurPresente[i] == 0 || i == myColor)
                distCouleurPresenteAfter[i] = 0;
            else
                distCouleurPresenteAfter[i] = computeDistanceTotEntreMemeCouleur(plateauSimule, couleurPresente[i]);

            sommeDistCouleurPrensenteAfter += distCouleurPresenteAfter[i];
        }

        // Enfin on calcule le score final a partir des valeurs avant calculées avant et après le mouvement
        if (myColorDistAfter - myColorDistBefore < 0)
            output = -1000;
        else
            output = ((myColorDistAfter - myColorDistBefore) * 5)
                    - (sommeDistCouleurPrensenteAfter - sommeDistCouleurPrensenteBefore);

        return output;
    }

    /**
     * 
     * @param plateau le plateau de jeu
     * @param couleur la couleur des pions dont l'on veut déterminé la somme des
     *                distances
     * @return la somme des distances minimums entre un territoire contenat un pion
     *         de couleur i et le territoire contenant un pion de couleur i le plus
     *         proche
     */
    public int computeDistanceTotEntreMemeCouleur(Terrain[] plateau, int couleur) {
        // initialisation des variables
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
                // on ne veut que les territoires avec la couleur souhaitée
                // ce territoire est le centre de depart
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
                    // on boucle a travers notre liste de centre
                    while (compteurCentre < lastLayer.size()) {
                        if (!wasCenter[lastLayer.get(compteurCentre)]) {
                            centre = plateau[lastLayer.get(compteurCentre)];
                            wasCenter[lastLayer.get(compteurCentre)] = true;
                            int compteurVoisin = 0;
                            // puis on boucle a travers le tableau de voisin des centres a la recherche de
                            // la couleur souhaité
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
     * @param plateau le plateau de jeu
     * @param idScr   le territoire source simulé
     * @param idDst   le territoire destinataire simulé
     * @return un plateau de jeu simulé avec le mouvement de terrtoire source idScr
     *         et de territoire destination idDst
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
     * @param _villages tableau des villages créés après le dernier mouvement
     * @return tableau d'ordre de création des villages
     */
    @Override
    public int[] ordre(int[] _villages) {
        int n = _villages.length;
        int[] combinaison = _villages;
        int[] indexes = new int[n];
        int[] output = new int[n];
        int gain = 0;

        for (int i = 0; i < n; i++)
            indexes[i] = 0;

        int i = 0;

        output = combinaison.clone();

        // on génère l'ensemble des combinaisons du tableau _villages
        // et on calcule le gain pour chaque combinaison
        // on prend la combinaison avec le score le plus grand
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
     * @param tab tableau à modifier
     * @param a   indice du premier élément à swap
     * @param b   indice du second élément à swap
     * @return le tableau tab avec les élément d'indice a et b permutés
     */
    private int[] swap(int[] tab, int a, int b) {
        int tmp = tab[a];
        tab[a] = tab[b];
        tab[b] = tmp;

        return tab;
    }

    /**
     * 
     * @param ordre la combinaison de villages à évaluer
     * @return un score égal à la différence du gain pour la couleur de l'IA et le
     *         gain de la meilleur 'autre' couleur
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
