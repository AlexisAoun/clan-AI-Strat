package strats;
import clans.Terrain;
import java.util.Random;

/**
 * Note (Anthony Fleury) : 
 * Cette strategie est a modifier et a adapter pour que vous puissiez rendre la votre.
 * Pour la modifier, avec le bouton droit, faite un Refactor, Rename et donnez lui le nom de votre classe 
 * StratX avec X votre numero de rendu. 
 * Une fois fait, completez CORRECTEMENT les methodes getName et getGroupes.
 * Vous n'aurez ensuite qu'a completer le code de votre propre strategie, a vous de jouer !
 */



/**
 * @author humeau
 */
public class Strat0 implements Strategie{
    Random rand;
    
    public Strat0 (){
        super();
        rand= new Random();
    }

    @Override
    public int[] mouvement(Terrain [] _plateau, int _myColor, int [] _colorScore, int _myScore, int _opponentScore, int [] _opponentMov, int [] _opponentVillages){
        int[] res= new int[2];
        int nbChoix= Tools.getNbSourceValide(_plateau);                          //on récupère le nb de source valide     
        int src= Tools.getSource(_plateau)[rand.nextInt(nbChoix)];               //on en tire une aléatoirement      
        int nbVoisin= Tools.getNbVoisinDispo(_plateau, src);                       //on récupère le nb de voisins de la source
        int dest= Tools.getVoisinsDispo(_plateau, src)[rand.nextInt(nbVoisin)];    //on en tire un aléatoirement
        
        //on retourne notre selection
        res[0]=src;
        res[1]=dest;
        return res;
    }
    

    @Override
    public int[] ordre( int[] _villages ){   
        Random rand = new Random();
        int a,tmp;
        // on melange le tableau des villages
        for (int i=0; i<_villages.length; i++){
            a=rand.nextInt(_villages.length);
            tmp=_villages[a];
            _villages[a]=_villages[i];
            _villages[i]=tmp;
        }
        //on retourne le tableau mélangé
        return _villages;
    }
    
    /**
     * Remplissez cette méthode avec le bon format expliqué ci-dessous
     * @return le nom des élèves (sous le format NOM1_NOM2) 
     * NOM1 et NOM2 sont uniquement les noms de famille de votre binome
     */
    public String getName(){
        return "Enseignant 0";
    }

    public String getGroupe(){
        return "P";
    }
    
}
