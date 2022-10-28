package TSP;

import Grafi.Strutture_Dati.Arco;
import Grafi.Strutture_Dati.Grafo.Grafo;
import Grafi.Strutture_Dati.Nodo;
import TSP.TSP_Sotto_Problema.Calcolatore_Sotto_Problemi;
import TSP.TSP_Sotto_Problema.Eccezione_Problema_Irrisolvibile;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TSP_Branching {
    Boolean Passaggi;
    String Log="";
    int nodi =1;
    int HTrovati=0;
    int TTrovati=0;
    int HAnalizzati=0;
    int TAnalizzati=0;
    private final Grafo<Integer, Integer, Integer> grafoPrincipale;
    private final Integer NodoCandidato;
    private ArrayList<Calcolatore_Sotto_Problemi> ListaSottoProblemi_H;
    private ArrayList<Calcolatore_Sotto_Problemi> ListaSottoProblemi_T;

    public TSP_Branching(Grafo<Integer, Integer, Integer> grafo, Boolean passaggi) {
        this(grafo, grafo.getNodes().get(0).getKey(),passaggi); //parte sempre dal nodo candidato 0
    }

    public TSP_Branching(Grafo<Integer, Integer, Integer> grafo, Integer candidateNode, Boolean passaggi) {
        this.grafoPrincipale = grafo.clone();
        this.ListaSottoProblemi_H = new ArrayList<>();
        this.ListaSottoProblemi_T = new ArrayList<>();
        this.NodoCandidato = candidateNode;
        this.Passaggi=passaggi;

    }

    public TSP_Risultati TSP_RisoluzioneProblema(boolean ignoraNodiDegree1) throws Eccezione_Problema_Irrisolvibile {
        List<Integer> nodiEliminabili = grafoPrincipale.getNodes()
                                             .stream()
                                             .filter(node -> node.getDegree() < 2)
                                             .map(Nodo::getKey)
                                             .collect(Collectors.toUnmodifiableList());
        String Log_Temp="";
        if (nodiEliminabili.size() > 0) {
            if (ignoraNodiDegree1) {
                nodiEliminabili.forEach(grafoPrincipale::removeNode);
                System.out.println("Remozione dei nodi {}." + nodiEliminabili.size());
            } else {
                throw new Eccezione_Problema_Irrisolvibile(nodiEliminabili);
            }
        }

        TSP_Risultati MinTSP_Risultati = new TSP_Risultati(grafoPrincipale, Integer.MAX_VALUE);//upper bound iniziale + infinito
        Calcolatore_Sotto_Problemi ProblemaIniziale = new Calcolatore_Sotto_Problemi(grafoPrincipale, NodoCandidato);
        if (ProblemaIniziale.cicloHamiltoniano()) {
            ListaSottoProblemi_H.add(HTrovati,ProblemaIniziale); HTrovati++;
        } else{
            ListaSottoProblemi_T.add(TTrovati,ProblemaIniziale); TTrovati++;
        }
        System.out.println("");
        MinTSP_Risultati.set_ConteggioNodiTotali(MinTSP_Risultati.get_ConteggioNodiTotali()+1);// contatore di nodi



        while(HTrovati> HAnalizzati || TTrovati> TAnalizzati) {
            Calcolatore_Sotto_Problemi problemaCorrente;

            if(HTrovati>HAnalizzati){//Carico un Sottoproblema dalla lista dei cicliHamiltoniani o 1-tree
                problemaCorrente = ListaSottoProblemi_H.get(HAnalizzati);
                HAnalizzati++;
            } else {
                problemaCorrente = ListaSottoProblemi_T.get(TAnalizzati);
                TAnalizzati++;
            }

            if (problemaCorrente.Feasible()) {  //se feasable
                if (problemaCorrente.cicloHamiltoniano()) {// se contiene ciclo hamiltoniano
                    Log_Temp= "("+problemaCorrente.getNodi()+") Ciclo hamiltoniano: "+problemaCorrente.stampaHamiltoniano()+" Costo="+problemaCorrente.get_LowerBound()+"\nPOSSIBILE SOLUZIONE\n";
                    aggiornaLog(Log_Temp);
                    if(Passaggi==true)
                        System.out.println(Log_Temp); //se contiene ciclo hamiltoniano
                    if (MinTSP_Risultati.get_Costo() >= problemaCorrente.get_LowerBound()) {// se nuova migliore soluzione
                        if (MinTSP_Risultati.get_Costo() > problemaCorrente.get_LowerBound())
                            MinTSP_Risultati.eliminaVecchiaSoluzioneMigliore(); //se è migliore di tutte le precedenti le cancello
                        MinTSP_Risultati.nuovaSoluzione(problemaCorrente.get_1Tree(), problemaCorrente.get_LowerBound());
                        MinTSP_Risultati.set_NodiChiusiPerSoluzioneMigliore(MinTSP_Risultati.get_NodiChiusiPerSoluzioneMigliore()+1);// settato nodo chiuso per ottimalità
                    }
                } else if (problemaCorrente.get_LowerBound() < MinTSP_Risultati.get_Costo()) {// siamo in un 1-tree non hamiltoniano in caso i nostro <lb sia minore del migliore
                    Log_Temp= "("+problemaCorrente.getNodi()+") 1-tree: "+ problemaCorrente.get_1Tree()+ " Costo="+problemaCorrente.get_LowerBound();
                    aggiornaLog(Log_Temp);
                    if(Passaggi==true)
                        System.out.println(Log_Temp);
                    int conteggioNuovoNodo = TSP_Branching.this.branchAndBound(problemaCorrente);//effettuo il braching
                    MinTSP_Risultati.set_ConteggioNodiTotali(MinTSP_Risultati.get_ConteggioNodiTotali()+conteggioNuovoNodo);
                    MinTSP_Risultati.set_NodiDiBranching(MinTSP_Risultati.get_NodiDiBranching()+1);
                } else {
                    Log_Temp= "("+problemaCorrente.getNodi()+") 1-tree: "+ problemaCorrente.get_1Tree()+ " Costo="+problemaCorrente.get_LowerBound()+"\nCHIUSO PER BOUND"+"\n";
                    aggiornaLog(Log_Temp);
                    if(Passaggi==true)
                        System.out.println(Log_Temp);
                    MinTSP_Risultati.set_NodiChiusiPerBound(MinTSP_Risultati.get_NodiChiusiPerBound()+1);// altrimenti chiudo per bound
                }
            } else {
                Log_Temp= "("+problemaCorrente.getNodi()+") 1-tree: "+ problemaCorrente.get_1Tree()+ " Costo="+problemaCorrente.get_LowerBound()+"\nCHIUSO PER UNFEASIBILITY"+"\n";
                aggiornaLog(Log_Temp);
                if(Passaggi==true)
                    System.out.println(Log_Temp);
                MinTSP_Risultati.set_NodiChiusiPerUnfeasibility(MinTSP_Risultati.get_NodiChiusiPerUnfeasibility()+1);// se non è feasable chiudo per unfeasibility
            }
        }

        MinTSP_Risultati.risultatoFinale();

        return MinTSP_Risultati;
    }


    private int branchAndBound(Calcolatore_Sotto_Problemi problemaCorrente) {
       String Log_temp="";
        Log_temp= "PROCEDURA DI BRANCING";
        aggiornaLog(Log_temp);
        if(Passaggi==true)
            System.out.println(Log_temp);
        HashMap<Integer, Integer> vettorePadri = new HashMap<>();
        dfs(problemaCorrente.get_1Tree().getNode(NodoCandidato), vettorePadri, problemaCorrente.get_1Tree());// ricerca in profondità per ottenere il vettore dei padri
        int conteggioNuoviNodi = 0;
        ArrayList<Arco<Integer, Integer>> sottoCiclo = new ArrayList<>();

        int nodoPartenza = NodoCandidato;
        int nodoArrivo = Integer.MAX_VALUE;

        while (nodoArrivo != NodoCandidato) { //fin quando il nodo di partenza non è il candidato
            nodoArrivo = vettorePadri.get(nodoPartenza);// nodoPartenza chiave hash map e nodoArrivo valore
            sottoCiclo.add(problemaCorrente.get_1Tree().getEdge(nodoArrivo, nodoPartenza)); // aggiunge l'arco nodoArrivo, to Node al sottociclo
            nodoPartenza = nodoArrivo; //aggiorna il nodoPartenza
        }
        sottoCiclo=riordinaSottoCiclo(sottoCiclo);

        Log_temp= "sottociclo: "+ stampaSottoCiclo(sottoCiclo);
        aggiornaLog(Log_temp);
        if(Passaggi==true)
            System.out.println(Log_temp);

        ArrayList<Arco<Integer, Integer>> archiObbligatori = problemaCorrente.get_ArchiObbligatori();
        ArrayList<Arco<Integer, Integer>> archiProibiti = problemaCorrente.get_ArchiProibiti();

        for (Arco<Integer, Integer> integerIntegerArco : sottoCiclo) {// cicla ogni arco del sottociclo
            if (!(problemaCorrente.get_ArchiObbligatori().contains(integerIntegerArco) ||  //controllo se l'arco del sottociclo non è contenuto nella lista degli archi mandatory
                  problemaCorrente.get_ArchiObbligatori().contains(integerIntegerArco.inverse()))) {
                nodi++;
                archiProibiti.add(integerIntegerArco); // aggiunge l'arco alla lista forbidden
                Calcolatore_Sotto_Problemi sottoProblema = new Calcolatore_Sotto_Problemi(grafoPrincipale,  //creo un nuovo subproblem
                                                           new ArrayList<>(archiObbligatori),
                                                           new ArrayList<>(archiProibiti),
                                                           NodoCandidato,
                                                          problemaCorrente.get_LivelloAlberoSottoProblemi() + 1,
                                                          nodi);

                if (sottoProblema.cicloHamiltoniano()) {
                    ListaSottoProblemi_H.add(HTrovati,sottoProblema);
                    HTrovati++;
                } else{
                    ListaSottoProblemi_T.add(TTrovati,sottoProblema);
                    TTrovati++;
                }

                Log_temp= "("+ sottoProblema.getNodi()+") EO:"+ stampaArchi(archiProibiti) +" E1:"+ stampaArchi(archiObbligatori);
                aggiornaLog(Log_temp);
                if(Passaggi==true)
                    System.out.println(Log_temp);
                conteggioNuoviNodi++;
                archiProibiti.remove(integerIntegerArco);// elimino l'arco del sottociclo in da forbidden
                archiObbligatori.add(integerIntegerArco); // aggiungo quell'arco in mandatory
            }
        }

        Log_temp= "CALCOLO DEI NUOVI GRAFI\n";
        aggiornaLog(Log_temp);
        if(Passaggi==true)
            System.out.println(Log_temp);

        return conteggioNuoviNodi;
    }

    public void aggiornaLog(String aggiornamento){
        this.Log= Log+"\n"+aggiornamento;
    }

    public void StampaLog(String nomeGrafo){
        String percorsoSoluzione="C:/Users/Gin/Desktop/OC/OC_tesina/implementazione/results";

        try {
            FileWriter file = new FileWriter(percorsoSoluzione+"/"+nomeGrafo+"Solution.csv");
            BufferedWriter output = new BufferedWriter(file);
            output.write(Log);
            output.close();
        }
        catch (Exception e) {
            e.getStackTrace();
        }
    }


    public  ArrayList<Arco<Integer, Integer>> riordinaSottoCiclo(ArrayList<Arco<Integer, Integer>> sottoCiclo){
        ArrayList<Arco<Integer, Integer>> risultato = new  ArrayList<Arco<Integer, Integer>>();
        int i=0;
        int MinimoD=Integer.MAX_VALUE;
        int MinimoA=Integer.MAX_VALUE;
        int pos=0;
        int posD=0;
        int posA=0;
        int g=0;
        String CD;
        String CA;
        String[] componente;
        String[] Da ;
        String[] A   ;
        String[] Etichetta;
        String da;
        String a;
        String etichetta;

        while (i<sottoCiclo.size()){
            componente = sottoCiclo.get(i).toString().split(",");
            Da  = componente[0].split("=");
            A    = componente[1].split("=");
            CD = Da[1];
            CA = A[1];
            if(Integer.parseInt(CD)<MinimoD){
                MinimoD=Integer.parseInt(CD); posD=i;
            }
            if(Integer.parseInt(CA)<MinimoA){
                MinimoA=Integer.parseInt(CA); posA=i;
            }
            i++;
        }

        if(MinimoD<= MinimoA){
            pos=posD;
            componente = sottoCiclo.get(pos).toString().split(",");
            Da  = componente[0].split("=");
            A    = componente[1].split("=");
            Etichetta = componente[2].split("=");
            da = Da[1];
            a = A[1];
            etichetta=Etichetta[1].substring(0, Etichetta[1].length() - 1);
            risultato.add(new Arco(Integer.parseInt(da),Integer.parseInt(a),Integer.parseInt(etichetta)));
        }else {
            pos=posA;
            componente = sottoCiclo.get(pos).toString().split(",");
            Da  = componente[0].split("=");
            A    = componente[1].split("=");
            Etichetta = componente[2].split("=");
            da = A[1];
            a = Da[1];
            etichetta=Etichetta[1].substring(0, Etichetta[1].length() - 1);
            risultato.add(new Arco(Integer.parseInt(da),Integer.parseInt(a),Integer.parseInt(etichetta)));
        }

        i=0;
        while (i<sottoCiclo.size()){
            if(i!=pos){
                componente = sottoCiclo.get(i).toString().split(",");
                Da  = componente[0].split("=");
                A    = componente[1].split("=");
                Etichetta = componente[2].split("=");
                CD = Da[1];
                CA = A[1];
                etichetta=Etichetta[1].substring(0, Etichetta[1].length() - 1);

                if(a.equals(CD)){
                    risultato.add(new Arco(Integer.parseInt(CD),Integer.parseInt(CA),Integer.parseInt(etichetta)));
                    a= CA; g++;
                }else if(a.equals(CA)){
                    risultato.add(new Arco(Integer.parseInt(CA),Integer.parseInt(CD),Integer.parseInt(etichetta)));
                    a= CD; g++;
                }
            }
            i++;
            if(i>=sottoCiclo.size() && g<sottoCiclo.size()-1)
                i=0;
        }

        return risultato;
    }




    public String stampaSottoCiclo(ArrayList<Arco<Integer, Integer>> sottoCiclo ){
        ArrayList archi = sottoCiclo;
        int i=0;
        int g=0;
        String da ="";
        String a = "";
        String CD="";
        String CA="";
        String[] componente = archi.get(i).toString().split(",");
        String[] Da  = componente[0].split("=");
        String[] A    = componente[1].split("=");
        String[] Etichetta = componente[2].split("=");
        da = Da[1];
        a = A[1];
        i++;
        String percorso = "("+da+")--("+a+")";
        while (i<archi.size()){
            componente = archi.get(i).toString().split(",");
            Da  = componente[0].split("=");
            A    = componente[1].split("=");
            CD = Da[1];
            CA = A[1];

            if(a.equals(CD)){
                percorso= percorso+"--("+CA+")";
                a= CA; g++;
            }else if(a.equals(CA)){
                percorso= percorso+"--("+CD+")";
                a= CD; g++;
            }
            i++;

            if(i==archi.size() && g!=archi.size()-1){
                i=1;
            }
        }

        return percorso;
    }

    public String stampaArchi(ArrayList<Arco<Integer, Integer>> Arco){
        ArrayList Archi = Arco;
        int i=0;
        String CD="";
        String CA="";
        String[] componente ;
        String[] Da  ;
        String[] A  ;
        String percorso = "[";
        while (i<Archi.size()){
            componente = Archi.get(i).toString().split(",");
            Da  = componente[0].split("=");
            A    = componente[1].split("=");
            CD = Da[1];
            CA = A[1];
            percorso= percorso + "("+CD+","+CA+")";
            i++;
        }

        return percorso+"]";
    }



    private void dfs(Nodo<Integer, Integer, Integer> nodoCorrente,
                     HashMap<Integer, Integer> vettorePadri,
                     Grafo<Integer, Integer, Integer> grafo) {
        for (Arco<Integer, Integer> arcoUscente : nodoCorrente.getEdges()) { //scorre la lista di adiacenze del nodo corrente  e per ogni arco di questa lista
            if (!vettorePadri.containsKey(arcoUscente.getTo())) { // se il vettore dei padri non contiene il nodo d'arrivo dell'arco analizzato e se non contiene il nodo corrente o
                if (!vettorePadri.containsKey(nodoCorrente.getKey()) || //se il nodo corrente non è uguale al nodo d'arrivo dell'arco analizzato
                    !vettorePadri.get(nodoCorrente.getKey()).equals(arcoUscente.getTo())) {

                    vettorePadri.put(arcoUscente.getTo(), nodoCorrente.getKey());           //inserisco questo nodo d'arrivo all'hash map
                    dfs(grafo.getNode(arcoUscente.getTo()), vettorePadri, grafo);           //essettuo una chiamata ricorsiva alla dfs analizzando questo nodo d'arrivo
                }
            }
        }
    }

}
