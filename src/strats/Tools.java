package strats;

import clans.Terrain;

/**
 *
 * @author guillaume
 */
public class Tools {

    static final String[] bonus = {"foret", "foret", "foret", "foret", "montagne", "montagne", "montagne", "champ", "champ", "plaine", "plaine", "tout"};
    static final String[] malus = {"montagne", "montagne", "montagne", "montagne", "plaine", "plaine", "plaine", "foret", "foret", "champ", "champ", "rien"};

    /**
     * 
     * @param plateau Le plateau de jeu
     * @return le nombre de source valide
     */
    public static int getNbSourceValide( Terrain [] plateau ) {
        int res = 0;
        for (int i = 0; i < 60; i++) {
            if (!plateau[i].estBloque() && !plateau[i].estVide() && !plateau[i].getVillage()) {
                res++;
            }
        }
        return res;
    }
    
    /**
     * 
     * @param plateau Le plateau de jeu
     * @return les differentes sources possibles.
     */
    public static int[] getSource( Terrain [] plateau ) {
        int[] res;
        int nbSourceValide = getNbSourceValide(plateau);
        res = new int[nbSourceValide];
        int tmp = 0;
        for (int i = 0; i < 60; i++) {
            if (!plateau[i].estBloque() && !plateau[i].estVide() && !plateau[i].getVillage()) {
                res[tmp] = i;
                tmp++;
            }
        }
        return res;
    }

    /**
     * @param plateau Le plateau de jeu
     * @param _terrain Le numero du terrain
     * @return le nombre de voisins vers lequels on peut aller depuis une source
     */
    public static int getNbVoisinDispo( Terrain [] plateau, int _terrain) {
        int res = 0;
        Terrain tmp;
        for (int i = 0; i < plateau[_terrain].getNbVoisins(); i++) {
            tmp = plateau[plateau[_terrain].getVoisin(i)];
            if (plateau[_terrain].getNbCabane() < 7) {
                if (!tmp.estVide())
                    res++;             
            }
            else {
                if (!tmp.estVide() && tmp.getNbCabane() >= plateau[_terrain].getNbCabane())
                    res++;
            }
        }
        return res;
    }
    
    /**
     * 
     * @param _terrain
     * @return un tableau contenant les voisins vers lequels on peut aller depuis une source
     */
    public static int[] getVoisinsDispo(Terrain [] plateau, int _terrain) {
        int[] res;
        int nbVoisinDispo = getNbVoisinDispo(plateau, _terrain);
        res = new int[nbVoisinDispo];
        Terrain tmp;
        int acc = 0;
        for (int i = 0; i < plateau[_terrain].getNbVoisins(); i++) {
            tmp = plateau[plateau[_terrain].getVoisin(i)];
            if (plateau[_terrain].getNbCabane() < 7) {
                if (!tmp.estVide()) {
                    res[acc] = plateau[_terrain].getVoisin(i);
                    acc++;
                }
            }
            else {
                if (!tmp.estVide() && tmp.getNbCabane() >= plateau[_terrain].getNbCabane()) {
                    res[acc] = plateau[_terrain].getVoisin(i);
                    acc++;
                }
            }
        }
        return res;
    }
    
    
    /**
     * 
     * @param plateau
     * @param _src
     * @return le nombre de villages créés si vous jouez depuis la source "_src"
     */
    public static int nbVillageCreeSi(Terrain [] plateau, int _src){
        int res=0;
        int v;
        for (int i=0; i<plateau[_src].getNbVoisins(); i++){
            v=plateau[_src].getVoisin(i);
            if((!plateau[v].estVide()) && ( getNbVoisinsNonVide(plateau, v) == 1 )){
                res++;
            } 
        }        
        return res;
    }
    
        /**
     * 
     * @param plateau
     * @param _terrain
     * @return le nombre de voisins non vides d'un terrain
     */
    public static int getNbVoisinsNonVide(Terrain[] plateau, int _terrain) {
        int res=0;
        for(int i=0; i< plateau[_terrain].getNbVoisins(); i++){
            if(!plateau[ plateau[_terrain].getVoisin(i) ].estVide())
                res++;
        }
        return res;
    }
    
    /**
     * 
     * @param plateau
     * @param _src
     * @param _dest
     * @param _ordre
     * @return caclcul les scores si vous jouez depuis la source "_src" vers la destination "_dest" avec un ordre "_ordre" de création de villages (si nécessaire)
     */
    public static int[] evaluerGain(Terrain [] plateau, int _src, int _dest, int[] _ordre){
        int[] res= {0, 0, 0, 0, 0};
        int nbVillage= nbVillageCreeSi(plateau, _src);
        int[] tmp;

        if(nbVillage>0){
            //on compte additionne les score pour chaque village
            for(int i=0; i < nbVillage; i++){
                tmp= scoreVillage( plateau, _src, _dest, _ordre[i], i);
                for (int j=0; j<5; j++){
                    res[j]+=tmp[j];
                }
            }
        }               
        return res;       
    }
    
    /**
     * 
     * @param plateau
     * @return compte le nombre de villagee sur le plateau
     */
    public static int countVillage ( Terrain [] plateau ){
        int nbVillage= 0;
        
        for ( int i= 0 ; i < plateau.length ; ++i ){
            if ( plateau[i].getVillage() ){
                nbVillage+= 1;
            }
        }
        return nbVillage;
    }
    
    /**
     * 
     * @param _src
     * @param _dest
     * @param _village
     * @param decalage
     * @return calcul les points gagnés par le village "_village" créé avec un décalage "decalage" si vous jouez depuis la source "_src" vers la destination "_dest"
     */
    public static int[] scoreVillage(Terrain [] plateau, int _src, int _dest, int _village, int decalage){
        int[] res=new int[5];
        int[] cabanes;
        int points=0;
        int villageCourant= countVillage(plateau);

        //Test destruction
        if( (villageCourant + decalage) < malus.length ){
            if( !plateau[_village].getType().equals( malus[villageCourant + decalage] )){
                cabanes=plateau[_village].getCabanes().clone();
                if(_dest==_village){
                    for(int i=0; i<5; i++){
                        cabanes[i]+=plateau[_src].getCabanes(i);
                    }
                }
                if( cabanes[0] > 0 &&  cabanes[1] > 0 && cabanes[2] > 0 
                        && cabanes[3] > 0 && cabanes[4] > 0 ){
                    for(int i=0; i<5; i++){
                        if( cabanes[i] == 1 )
                            cabanes[i]= 0;
                    }
                }
                for( int i=0; i<5; i++ )
                    points+=cabanes[i];
                
                if(plateau[_village].getType().equals(bonus[villageCourant + decalage]))
                    points+= age( villageCourant+decalage );
                
                for(int i=0; i<5; i++)
                    if(cabanes[i]>0)
                        res[i]=points;
            }
        }
        return res;
    }

    /**
     * 
     * @param _nb
     * @return l'age dans lequel on se situerai apres la creation de "_nb" villages
     */
    public static int age(int _nb){
        int res = 1;
        if (_nb > 10) {
            res = 5;
        } else if (_nb > 8) {
            res = 4;
        } else if (_nb > 6) {
            res = 3;
        } else if (_nb > 3) {
            res = 2;
        }
        return res;
    }

    /**
     * 
     * @param plateau
     * @param _src
     * @return le tableau des villages créés si vous jouez depuis la source "_src"
     */
    public static int[] listeVillagesCreesSi( Terrain [] plateau, int _src){
        int[] res= new int[nbVillageCreeSi(plateau, _src)];
        int v;
        int cpt=0;
        for (int i=0; i<plateau[_src].getNbVoisins(); i++){
            v=plateau[_src].getVoisin(i);
            if( (!plateau[v].estVide()) && (getNbVoisinsNonVide(plateau, v)==1) ){
                res[cpt]=v;
                cpt++;
            } 
        }        
        return res;
    }
}
