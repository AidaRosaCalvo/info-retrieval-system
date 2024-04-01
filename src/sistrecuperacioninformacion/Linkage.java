
package sistrecuperacioninformacion;

import java.util.ArrayList;

/**
 *
 * @author Aida Rosa
 */
public class Linkage {
    public static double[][] calculateDistanceMatrix(ArrayList<DocDetails> documents) {
        int numDocuments = documents.size();
        double[][] distanceMatrix = new double[numDocuments][numDocuments];

        for (int i = 0; i < numDocuments; i++) {
            for (int j = 0; j < numDocuments; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0.0;
                } else {
                    double distance = calculateDocumentDistance(documents.get(i), documents.get(j));
                    distanceMatrix[i][j] = distance;
                }
            }
        }

        return distanceMatrix;
    }

    private static double calculateDocumentDistance(DocDetails doc1, DocDetails doc2) {
        // compara la cantidad de tokens compartidos
        ArrayList<String> tokens1 = doc1.getToken();
        ArrayList<String> tokens2 = doc2.getToken();

        int intersectionSize = 0;
        for (String token : tokens1) {
            if (tokens2.contains(token)) {
                intersectionSize++;
            }
        }

        int unionSize = tokens1.size() + tokens2.size() - intersectionSize;
        return 1.0 - (double) intersectionSize / unionSize;
    }

    public static ArrayList<Cluster> performLinkageClustering(ArrayList<DocDetails> documents, double[][] distanceMatrix) {
        int numDocuments = documents.size();

        // Inicializar una lista de clusters, donde cada documento se encuentra en un cluster individual
        ArrayList<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < numDocuments; i++) {
            clusters.add(new Cluster(i));
        }

        // Realizar el proceso de clustering aglomerativo
        while (clusters.size() > 1) {
            double minDistance = Double.MAX_VALUE;
            int minI = -1;
            int minJ = -1;

            // Encontrar los dos clusters más cercanos
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double distance = calculateClusterDistance(clusters.get(i), clusters.get(j), distanceMatrix);
                    if (distance < minDistance) {
                        minDistance = distance;
                        minI = i;
                        minJ = j;
                    }
                }
            }

            // Fusionar los dos clusters más cercanos en un nuevo cluster
            Cluster mergedCluster = mergeClusters(clusters.get(minI), clusters.get(minJ));
            clusters.remove(minJ);
            clusters.remove(minI);
            clusters.add(mergedCluster);
        }

        return clusters;
    }

    private static double calculateClusterDistance(Cluster cluster1, Cluster cluster2, double[][] distanceMatrix) {
        // Implementa aquí la medida de distancia con ejemplo básico que utiliza el enlace simple:
        double minDistance = Double.MAX_VALUE;
        for (int index1 : cluster1.getIndices()) {
            for (int index2 : cluster2.getIndices()) {
                double distance = distanceMatrix[index1][index2];
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }

    private static Cluster mergeClusters(Cluster cluster1, Cluster cluster2) {
        // Fusionar los dos clusters en uno nuevo
        ArrayList<Integer> mergedIndices = new ArrayList<>();
        mergedIndices.addAll(cluster1.getIndices());
        mergedIndices.addAll(cluster2.getIndices());
        return new Cluster(mergedIndices);
    }
}
