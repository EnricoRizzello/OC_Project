package TSP;

import Grafi.Strutture_Dati.Grafo.Grafo;

import java.util.ArrayList;

public class TSP_Risultati {

    private int conteggioNodiTotali = 0;
    private int conteggioNodiChiusiOttimalità = 0;
    private int conteggioNodiChiusiBound = 0;
    private int conteggioNodiChiusiUnfeasibility = 0;
    private int conteggioNodiBranching = 0;
    private ArrayList<Grafo<Integer, Integer, Integer>> grafiSoluzione = new ArrayList<Grafo<Integer, Integer, Integer>>();
    private int costo;
    private statoRisultante stato = statoRisultante.Irrisolto;


    public TSP_Risultati(Grafo<Integer, Integer, Integer> graph, int cost) {
        this.costo = cost;
        this.grafiSoluzione.add(graph);

    }


    public int get_Costo() {
        return costo;
    }

    public int get_ConteggioNodiTotali() {
        return conteggioNodiTotali;
    }

    public int get_NodiChiusiPerSoluzioneMigliore() {
        return conteggioNodiChiusiOttimalità;
    }

    public int get_NodiChiusiPerBound() {
        return conteggioNodiChiusiBound;
    }

    public int get_NodiChiusiPerUnfeasibility() {
        return conteggioNodiChiusiUnfeasibility;
    }

    public int get_NodiDiBranching() {
        return conteggioNodiBranching;
    }

    public void nuovaSoluzione(Grafo<Integer, Integer, Integer> graph, int cost) {
        this.grafiSoluzione.add(graph);
        this.costo = cost;
        this.stato = statoRisultante.risolvibile;
    }

    public void eliminaVecchiaSoluzioneMigliore(){
        this.grafiSoluzione.clear();
        this.costo = Integer.MAX_VALUE;
    }



    public void risultatoFinale() {
        if (this.stato == statoRisultante.risolvibile) {
            this.stato = statoRisultante.risolto;
        } else if (this.stato == statoRisultante.Irrisolto) {
            this.stato = statoRisultante.irrisolvibile;
        } else {
            throw new IllegalStateException("Cannot finalize a solution already finalized.");
        }
    }

    public String get_PercorsoSoluzione() throws IllegalStateException {
        if (stato == statoRisultante.irrisolvibile) {
            throw new IllegalStateException("The related problem has been deemed unsolvable, so no path exists.");
        } else if (stato == statoRisultante.Irrisolto) {
            throw new IllegalStateException("A solution to the related problem has yet to be found.");
        }

        String percorso="";
        int k=0;
        while(k<grafiSoluzione.size()){

            ArrayList archi = grafiSoluzione.get(k).getEdges();
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
            percorso = percorso+ "("+da+")--"+etichetta+"--("+a+")";

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
        k++;

        percorso=percorso+"\n";
        }

        return percorso;
    }

    @Override
    public String toString() {
        String soluzione="";
        switch (this.stato) {
            case risolto:

                         if(contaMiglioriPercorsi(get_PercorsoSoluzione())==1){
                            soluzione="E'stata trovato una soluzione migliore con costo " + costo+ " e percorso\n"+ get_PercorsoSoluzione() +"\n";
                         } else{
                            soluzione="Sono state trovate "+ contaMiglioriPercorsi(get_PercorsoSoluzione())+ " soluzioni migliori con costo " + costo+ " e percorso\n"+ get_PercorsoSoluzione() +"\n";
                         }
                    break;
            case risolvibile:
                            if(contaMiglioriPercorsi(get_PercorsoSoluzione())==1){
                                 soluzione="E'stata trovato una soluzione temporanea migliore con costo " + costo+ " e percorso\n"+ get_PercorsoSoluzione() +"\n";
                            } else{
                                soluzione="Sono state trovate "+ contaMiglioriPercorsi(get_PercorsoSoluzione())+ " soluzioni temporanee migliori con costo " + costo+ " e percorso\n"+ get_PercorsoSoluzione() +"\n";
                            }
                    break;
            case irrisolvibile:
                            soluzione= "Il problema non può essere risolto e non ha quindi alcuna migliore soluzione";
                    break;

            case Irrisolto:
                        soluzione= "Non è stata ancora trovata alcuna soluzione";

            default:   String.format("");
                    break;
        };

   return soluzione;
    }


    private static int contaMiglioriPercorsi(String percorsi){
        String[] numeroPercorsi = percorsi.split("\r\n|\r|\n");
        return  numeroPercorsi.length;
    }

    public String get_Statistiche() {
        return String.format(""" 
                                  Durante la ricerca sono stati creati:
                                  (%d) Nodi intermedi per la generazione di Branchess;
                                  (%d) Nodi chiusi per possibile soluzione;
                                  (%d) Nodi chiusi per Bound;
                                  (%d) Nodi chiusi per Unfeasibility.
                                 """,
                             this.get_NodiDiBranching(),
                             this.get_NodiChiusiPerSoluzioneMigliore(),
                             this.get_NodiChiusiPerBound(),
                             this.get_NodiChiusiPerUnfeasibility());
    }

    public void set_ConteggioNodiTotali(int conteggioNodiTotali) {
        this.conteggioNodiTotali = conteggioNodiTotali;
    }

    public void set_NodiChiusiPerSoluzioneMigliore(int conteggioNodiChiusiOttimalità) {
        this.conteggioNodiChiusiOttimalità = conteggioNodiChiusiOttimalità;
    }

    public void set_NodiChiusiPerBound(int conteggioNodiCHiusiBound) {
        this.conteggioNodiChiusiBound = conteggioNodiCHiusiBound;
    }

    public void set_NodiChiusiPerUnfeasibility(int conteggioNodiChiusiUnfeasibility) {
        this.conteggioNodiChiusiUnfeasibility = conteggioNodiChiusiUnfeasibility;
    }

    public void set_NodiDiBranching(int conteggioNodiBranching) {
        this.conteggioNodiBranching = conteggioNodiBranching;
    }

    public enum statoRisultante {
        risolto, risolvibile, irrisolvibile, Irrisolto
    }

}
