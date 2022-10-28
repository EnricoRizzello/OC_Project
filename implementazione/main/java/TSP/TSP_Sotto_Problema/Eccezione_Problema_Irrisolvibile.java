package TSP.TSP_Sotto_Problema;

import java.util.Collections;
import java.util.List;

public class Eccezione_Problema_Irrisolvibile extends Exception {
    public final List<Integer> chiaviNodiInvalidi;

    public Eccezione_Problema_Irrisolvibile(List<Integer> oneWayNodesKeys) {
        this.chiaviNodiInvalidi = Collections.unmodifiableList(oneWayNodesKeys);
    }
}
