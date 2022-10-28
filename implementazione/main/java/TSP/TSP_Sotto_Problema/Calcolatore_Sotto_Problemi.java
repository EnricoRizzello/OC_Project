package TSP.TSP_Sotto_Problema;

import Grafi.Strutture_Dati.Grafo.Grafo_Eccezioni.Eccezione_Nodo_Mancante;
import Grafi.Strutture_Dati.Arco;
import Grafi.Strutture_Dati.Grafo.Grafo;
import Grafi.Strutture_Dati.Nodo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static Grafi.MST.MST_Kruskal.MST_Kruskal;

public class Calcolatore_Sotto_Problemi  {

    private Grafo<Integer, Integer, Integer> grafoOriginale;
    private ArrayList<Arco<Integer, Integer>> archiObblicatori;
    private ArrayList<Arco<Integer, Integer>> archiProibiti;
    private Integer nodoCandidato;
    private Integer livelloAlberoSottoProblema;
    private Grafo<Integer, Integer, Integer> oneTree;
    private int lowerBound;
    private boolean contieneCicloHamiltoniano;
    private boolean feasible;
    private Integer Nodi;


    public Calcolatore_Sotto_Problemi(Grafo<Integer, Integer, Integer> originalGraph, Integer nodoCandidato) {
        this(originalGraph, new ArrayList<>(0), new ArrayList<>(0), nodoCandidato, 0,1);//E0 E1
    }

    public Calcolatore_Sotto_Problemi(Grafo<Integer, Integer, Integer> grafoOriginale,
                                      ArrayList<Arco<Integer, Integer>> archiObbligatori,
                                      ArrayList<Arco<Integer, Integer>> archiProibiti,
                                      Integer nodoCandidato,
                                      Integer slivelloAlberoSottoProblema,
                                      Integer Nodi) {
        this.archiObblicatori = archiObbligatori;
        this.archiProibiti = archiProibiti;
        this.grafoOriginale = grafoOriginale;
        this.nodoCandidato = nodoCandidato;
        this.livelloAlberoSottoProblema = slivelloAlberoSottoProblema;
        this.Nodi = Nodi;

        this.oneTree = calcolo11Tree();//computazione dell'MST e 1-tree
        this.lowerBound = calcoloCosto1Tree(); // somma di tutti gli archi del MST
        this.contieneCicloHamiltoniano = controlloCicloHamiltoniano();
        this.feasible = oneTree.getNodes().size() == grafoOriginale.getNodes().size() &&  //controllo se feasable se numero archi e vertici 1-tree = originale
                        oneTree.getEdges().size() == grafoOriginale.getNodes().size();
    }

    private Grafo<Integer, Integer, Integer> calcolo11Tree() {
        // Si calcola l'MST deò grafo originale senza il nodo candidato che verrà aggiunto dopo insieme ai due migliori archi incidenti

        //Passo (1) MST
        ArrayList<Arco<Integer, Integer>> veriArchiObbligatori = (ArrayList<Arco<Integer, Integer>>) archiObblicatori.clone();//lista di archi copiata
        for (Arco<Integer, Integer> obbligatori : archiObblicatori) { //elimina e tutti gli archi incidenti del nodo candidato
            if (obbligatori.getTo().equals(nodoCandidato) || obbligatori.getFrom().equals(nodoCandidato)) {
                veriArchiObbligatori.remove(obbligatori);
            }
        }

        //eseguo kruskal
        Grafo<Integer, Integer, Integer> MST_Corrente = MST_Kruskal(grafoOriginale.clone()
                                                                          .removeNode(nodoCandidato),// eliminazione nodo candidato
                                                             comparatoreArchi.getInstance(),
                                                             veriArchiObbligatori,// copia archi incidenti eliminati
                archiProibiti).addNode(nodoCandidato);// aggiunge il nodo candidato  all'MST

        // Filtro tutti gli arci incidenti sul nodo candidato  per entrambe le liste
        List<Arco<Integer, Integer>> archiIncidentiObbligatori = archiObblicatori.stream()
                                                                            .filter((arco) -> arco.isIncidentFor(nodoCandidato))
                                                                            .collect(Collectors.toList());
        List<Arco<Integer, Integer>> archiIncidentiProibiti = archiProibiti.stream()
                                                                            .filter((arco) -> arco.isIncidentFor(nodoCandidato))
                                                                            .collect(Collectors.toList());

           //Passo (2)
        if (MST_Corrente.getNodes().size() == grafoOriginale.getNodes().size()) {
            Arco<Integer, Integer> arcoPrimo = null, arcoSecondo = null;// crea i due archi da aggiungere inizializzati a nulli
            if (archiIncidentiObbligatori.size() >= 2) {  // se ci sono due archi mandatory incidenti sul nodo candidato
                for (Arco<Integer, Integer> arco : archiIncidentiObbligatori) {
                    if (arcoPrimo == null) { // calcolo degli archi a distanza minima tra quelli incidenti sul nodo candidato e aggiungo i primi due
                        arcoPrimo = arco;
                    } else if (arcoSecondo == null) {
                        arcoSecondo = arco;
                    } else {
                        if (arcoPrimo.getLabel() < arcoSecondo.getLabel()) {
                            if (arco.getLabel() < arcoSecondo.getLabel()) {
                                arcoSecondo = arco;
                            }
                        } else {
                            if (arco.getLabel() < arcoPrimo.getLabel()) {
                                arcoPrimo = arco;
                            }
                        }
                    }
                }
            } else if (archiIncidentiObbligatori.size() == 1) {
                arcoPrimo = archiIncidentiObbligatori.get(0);// se ho un solo arco incidente setto il primo arco

                for (Arco<Integer, Integer> arco : grafoOriginale.getEdges()) { // aggiunge il miglior arco del grafo originale incidente al nodo candidato non presente nella lista forbidden e diverso dal first
                    if (!(archiIncidentiProibiti.contains(arco) || archiIncidentiProibiti.contains(arco.inverse())) && (arco.isIncidentFor(nodoCandidato)) && (!(arcoPrimo.equals(arco) || arcoPrimo.inverse()
                                                                                                                                                                                                    .equals(arco)))) {
                        if (arcoSecondo == null) {
                            arcoSecondo = arco;
                        } else if (arcoSecondo.getLabel() > arco.getLabel()) {
                            arcoSecondo = arco;
                        }
                    }
                }

            } else {//in caso non abbia alcun arco incidenteMandatory simile a prima ma due archi
                for (Arco<Integer, Integer> arco : grafoOriginale.getEdges()) {
                    if (!(archiIncidentiProibiti.contains(arco) || archiIncidentiProibiti.contains(arco.inverse())) && arco.isIncidentFor(nodoCandidato)) {
                        if (arcoPrimo == null) {
                            arcoPrimo = arco;
                        } else if (arcoSecondo == null) {
                            arcoSecondo = arco;
                        } else {
                            if (arcoPrimo.getLabel() < arcoSecondo.getLabel()) {
                                if (arco.getLabel() < arcoSecondo.getLabel()) {
                                    arcoSecondo = arco;
                                }
                            } else {
                                if (arco.getLabel() < arcoPrimo.getLabel()) {
                                    arcoPrimo = arco;
                                }
                            }
                        }
                    }
                }
            }

            if (arcoPrimo != null && arcoSecondo != null) {//controllo se sono stati effettivamente trovati e vengono aggiunti all'MST
                try {
                    MST_Corrente.addEdge(arcoPrimo).addEdge(arcoSecondo);
                } catch (Eccezione_Nodo_Mancante e) {
                    e.printStackTrace();
                }
            }
        }

        return MST_Corrente;
    }

    private int calcoloCosto1Tree() {  //sommo i pesi degli archi del MST e ritorno
        return this.oneTree.getEdges()
                           .stream()
                           .mapToInt(Arco::getLabel)
                           .sum();
    }

    public String stampaHamiltoniano(){
        ArrayList archi = oneTree.getEdges();
        int i=0;
        int g=0;
        String da;
        String a;
        String etichetta;
        String CD;
        String CA;
        String CE;
        String[] componente = archi.get(i).toString().split(",");
        String[] Da  = componente[0].split("=");
        String[] A    = componente[1].split("=");
        String[] Etichetta = componente[2].split("=");
        da = Da[1];
        a = A[1];
        etichetta = Etichetta[1].substring(0, Etichetta[1].length() - 1);

        i++;
        String percorso = "("+da+")--"+etichetta+"--("+a+")";
        while (i<archi.size()){
            componente = archi.get(i).toString().split(",");
            Da  = componente[0].split("=");
            A    = componente[1].split("=");
            Etichetta = componente[2].split("=");
            CD = Da[1];
            CA = A[1];
            CE = Etichetta[1].substring(0, Etichetta[1].length() - 1);
            if(a.equals(CD)){
                percorso= percorso+"--"+CE+"--("+CA+")";
                a= CA; g++;
            }else if(a.equals(CA)){
                percorso= percorso+"--"+CE+"--("+CD+")";
                a= CD; g++;
            }
            i++;
            if(i==archi.size() && g!=archi.size()-1)
                i=1;

        }

        return percorso;
    }



    private boolean controlloCicloHamiltoniano() { // controllo se è un ciclo Hamiltoniano verificando che il grado di tutti i nodi sia =2
        return oneTree.getNodes()
                      .stream()
                      .map(Nodo::getDegree)
                      .allMatch(degree -> degree == 2);
    }



    public ArrayList<Arco<Integer, Integer>> get_ArchiObbligatori() {
        return archiObblicatori;
    }

    public ArrayList<Arco<Integer, Integer>> get_ArchiProibiti() {
        return archiProibiti;
    }

    public Integer getNodi() {
        return Nodi;
    }

    public Grafo<Integer, Integer, Integer> get_1Tree() {
        return oneTree;
    }

    public int get_LowerBound() {
        return lowerBound;
    }

    public boolean cicloHamiltoniano() {
        return contieneCicloHamiltoniano;
    }

    public boolean Feasible() {
        return feasible;
    }

    public Integer get_LivelloAlberoSottoProblemi() {
        return livelloAlberoSottoProblema;
    }

    public String toString() {
        String stringa = get_1Tree().toString();
        stringa = stringa + "\n costo: " + get_LowerBound();
        stringa = stringa + "\n Archi forzati: " + get_ArchiObbligatori().toString();
        stringa = stringa + "\n Archi vietati: " + get_ArchiProibiti().toString();
        return stringa;
    }
// comparatore di archi interi
    private static class comparatoreArchi implements Comparator<Arco<Integer, Integer>> {

        private static comparatoreArchi instance = null;

        public synchronized static comparatoreArchi getInstance() {
            if (instance == null) {
                instance = new comparatoreArchi();
            }

            return instance;
        }

        @Override
        public int compare(Arco<Integer, Integer> o1, Arco<Integer, Integer> o2) {
            return o1.getLabel().compareTo(o2.getLabel()); //comparison pesi degli archi
        }
    }

}
