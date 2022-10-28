import Grafi.Strutture_Dati.Grafo.Grafo;
import TSP.TSP_Branching;
import TSP.TSP_Risultati;
import TSP.TSP_Sotto_Problema.Eccezione_Problema_Irrisolvibile;

import java.io.*;
import java.util.Iterator;

public class Calcolo_Percorso {

    public static void main(String[] args) throws IOException {
        String[] impostazioniGrafo = selezioneGrafo();
        boolean passaggi= Boolean.parseBoolean(impostazioniGrafo[1]);
        boolean csv =Boolean.parseBoolean(impostazioniGrafo[2]);;
        boolean rimozioneNodiInvalidi = Boolean.parseBoolean(impostazioniGrafo[2]);;

        Grafo<Integer, Integer, Integer> graph = caricaGrafo(impostazioniGrafo[0]);
        TSP_Branching risoluzione_BranchAndBound = new TSP_Branching(graph,passaggi);

        long tempoPartenza = System.currentTimeMillis();

        TSP_Risultati risultati = null;
        try {
            risultati = risoluzione_BranchAndBound.TSP_RisoluzioneProblema(rimozioneNodiInvalidi);
        } catch (Eccezione_Problema_Irrisolvibile e) {
            System.err.println("Alcuni nodi hanno un solo arco incidente " +
                               "La seguente è la lista dei nodi problematici:");
            System.err.println(e.chiaviNodiInvalidi.toString());
            System.exit(1);
        }

        long tempoEsecuzione = System.currentTimeMillis() - tempoPartenza;

        System.out.println(risultati.toString());
        risoluzione_BranchAndBound.aggiornaLog(risultati.toString());
        System.out.println(risultati.get_Statistiche());
        risoluzione_BranchAndBound.aggiornaLog(risultati.get_Statistiche());
        System.out.println("Tempo complessivo di esecuzione: " + tempoEsecuzione + " millisecondi");
        risoluzione_BranchAndBound.aggiornaLog("Tempo complessivo di esecuzione: " + tempoEsecuzione + " millisecondi");

        if(csv==true)
           risoluzione_BranchAndBound.StampaLog(nomeGrafo().substring(0, nomeGrafo().length() - 4));

    }


    private static Grafo<Integer, Integer, Integer> caricaGrafo(String percorsoFile) throws IOException {
        File fileGrafo = new File(percorsoFile);
        Grafo<Integer, Integer, Integer> grafo = new Grafo<>(false);

        try (BufferedReader lineReader = new BufferedReader(new FileReader(fileGrafo))) {
            Iterator<String> lineIterator = lineReader.lines().iterator();

            while (lineIterator.hasNext()) {
                String impostazione = lineIterator.next();
                String[] componenti = impostazione.split(" ");
                int da = Integer.parseInt(componenti[0]);
                int a = Integer.parseInt(componenti[1]);
                int peso;

                try {
                    peso = Integer.parseInt(componenti[2]);
                } catch (NumberFormatException e) {
                    peso = (int) (Float.parseFloat(componenti[2]) * 100);
                }
                grafo.addNodesEdge(da, a, peso);

            }
        } catch (FileNotFoundException e) {
            System.out.println("No file exists at the specified path");
            System.exit(1);
        }

        return grafo;
    }

    private static  String[] selezioneGrafo() {
        String percorso="C:/Users/Gin/Desktop/OC_tesina/implementazione/resources/";
        BufferedReader reader;
        String [] risultati = new String[4];
        try {
            reader = new BufferedReader(new FileReader(
                    "C:/Users/Gin/Desktop/OC_tesina/implementazione/nomeGrafo.txt"));

            percorso = percorso+reader.readLine();
            risultati[0]= percorso;
            risultati[1]= reader.readLine();
            risultati[2]= reader.readLine();
            risultati[3]= reader.readLine();

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return risultati;
    }

    private static  String nomeGrafo() {
        String percorso="";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "C:/Users/Gin/Desktop/OC_tesina/implementazione/nomeGrafo.txt"));

            percorso = reader.readLine();


            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return percorso;
    }

}
