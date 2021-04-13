package strats;
import clans.Terrain;

/**
 * @author humeau
 */
public interface Strategie {
    
    /**
     * @param _plateau
     * @param color
     * @param _colorScore
     * @param _myScore
     * @param _opponentScore
     * @param _opponentMov movement (source, destination) au tour précédent.
     * @param _opponentVillages la liste ordonée des villages construits au tour précédent.
     * @return un tableau de 2 entiers, le 1er sera la source de votre coup, et le second sera la destination
     */
    public int[] mouvement( Terrain [] _plateau, int _myColor, int [] _colorScore, int _myScore, int _opponentScore, int [] _opponentMov, int [] _opponentVillages );
    
    /**
     * @param _villages
     * @return un tableau correspondant à l'ordre dans laquelle les villages seront créés (si nécessaire)
     */
    public int[] ordre( int[] _villages );
    
    /**
     * IMPORTANT!!!
     * Remplissez correctement cette méthode avec le bon format expliqué ci-dessous (-2 points si consigne non respectée)
     * @return le nom des élèves (sous le format NOM1 NOM2, ex: "FLEURY MICHON") 
     * NOM1 et NOM2 sont uniquement les noms de famille de votre binome
     * (si nom de famille identique à un autre élève, vous pouvez ajouter l'initiale de votre prénom, ex: A.FLEURY MICHON
     */
    public String getName();

    /**
     * IMPORTANT!!!
     * Remplissez correctement cette méthode avec le bon format expliqué ci-dessous (-2 points si consigne non respectée)
     * @return le groupe de TD/TP (1, 2, 3, 4, 5, 6 ou P pour les enseignants)
     * Uniquement une lettre ou un chiffre (ex: "1" et surtout pas "groupe 1")
     */    
    public String getGroupe();
    
}
