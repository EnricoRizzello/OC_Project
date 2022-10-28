package Grafi.MST;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// UnionFind serve per unire due alberi di tipo generico
public class MST_UnionFind<T> {
    private Map<T, T> parents = new HashMap<>();
    private Map<T, Integer> ranghi = new HashMap<>();

    /*
     * START getter/setter
     */
    public Map<T, T> getParents() {
        return parents;
    }

    public void setParents(Map<T, T> parents) {
        this.parents = parents;
    }

    public Map<T, Integer> getRanghi() {
        return ranghi;
    }

    public void setRanghi(Map<T, Integer> ranghi) {
        this.ranghi = ranghi;
    }
    /*
     * END getter/setter
     */

   // unione dei due alberi (union)
    public void unione( T albero1,  T albero2) {

        collega(cercaSet(albero1), cercaSet(albero2));
    }


    // collega due alberi (link)
    private void collega( T albero1,  T albero2) throws NullPointerException {
        int rango1, rango2;

        if (ranghi.get(albero1) == null || ranghi.get(albero2) == null) throw new NullPointerException();

        rango1 = ranghi.get(albero1);
        rango2 = ranghi.get(albero2);

        if (rango1 < rango2)
            parents.put(albero1, albero2);
        else {
            parents.put(albero2, albero1);
            if (rango1 == rango2)
                ranghi.put(albero1, ranghi.get(albero1) + 1);
        }
    }


    // costruisci uno o più nodi dell'albero (makeSet)
    public void costruisciSet( T elemento) throws NullPointerException {
        if (elemento == null) throw new NullPointerException();

        parents.put(elemento, elemento);
        ranghi.put(elemento, 0);
    }

    public void costruisciSet( ArrayList<T> elementi) throws NullPointerException {
        for (T elemento : elementi) {
            if (elemento == null) throw new NullPointerException();

            parents.put(elemento, elemento);
            ranghi.put(elemento, 0);
        }
    }


    /**
     * FindSet looks for the root
     *
     * @param u node
     * @return the root
     */
    // cerca la radice (cercaSet)
    public T cercaSet(T u) {

        if (parents.get(u) == null)
            return null;

        if (!u.equals(parents.get(u)))
            parents.put(u, cercaSet(parents.get(u)));
        return parents.get(u);
    }

    @Override
    public String toString() {
        return "<UnionFind\np " + parents.toString() + "\nr " + ranghi.toString() + "\n>";
    }
}