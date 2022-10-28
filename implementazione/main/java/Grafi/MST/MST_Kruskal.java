package Grafi.MST;

import Grafi.Strutture_Dati.Arco;
import Grafi.Strutture_Dati.Grafo.Grafo;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class MST_Kruskal {

    public static <K, V, E>  Grafo<K, V, E> MST_Kruskal( Grafo<K, V, E> grafo,
                                                                 Comparator<Arco<K, E>> Arco,
                                                                 List<Arco<K, E>> archiObbligatori,
                                                                 List<Arco<K, E>> archiProibiti) {

        Grafo<K, V, E> MST_Corrente = new Grafo<>(false);// creo nuovo grafo
        ArrayList<Arco<K, E>> listaArchi;//creo edge list
        MST_UnionFind<K> MST_UnionFind = new MST_UnionFind<>();

        grafo.getNodes().forEach(n -> MST_UnionFind.costruisciSet(n.getKey()));

        listaArchi = grafo.getEdges();
        listaArchi.sort(Arco);

        for (Arco<K, E> arco : archiObbligatori) {
            MST_Corrente.addNodesEdge(arco.getFrom(), arco.getTo(), arco.getLabel());
            MST_UnionFind.unione(arco.getFrom(), arco.getTo());
        }

        for (Arco<K, E> arco : listaArchi) {
            // Ignora gli arcchi che non devono essere aggiunti in nessun caso
            // Ignora gli archi già aggiunti
            if (archiProibiti.contains(arco) || archiProibiti.contains(arco.inverse()) ||
                archiObbligatori.contains(arco) || archiObbligatori.contains(arco.inverse())){
                continue;
            }

            if (MST_UnionFind.cercaSet(arco.getFrom()) != MST_UnionFind.cercaSet(arco.getTo())) {
                MST_Corrente.addNodesEdge(arco.getFrom(), arco.getTo(), arco.getLabel());
                MST_UnionFind.unione(arco.getFrom(), arco.getTo());
            }
        }
        return MST_Corrente;
    }
}
