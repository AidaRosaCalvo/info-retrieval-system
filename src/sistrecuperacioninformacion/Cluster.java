
package sistrecuperacioninformacion;

import java.util.ArrayList;

/**
 *
 * @author Aida Rosa
 */
public class Cluster {
    
    private ArrayList<Integer> indices;

    public Cluster(ArrayList<Integer> indices) {
        this.indices = indices;
    }

    public Cluster(int index) {
        this.indices = new ArrayList<>();
        this.indices.add(index);
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }
}
