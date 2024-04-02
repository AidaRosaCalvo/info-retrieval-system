
package sistrecuperacioninformacion;

import java.util.ArrayList;

/**
 *
 * @author Aida Rosa
 */
public class Kmeans {
    
    public static ArrayList<ArrayList<DocDetails>> kmeans(ArrayList<DocDetails> documents, int k) {
       
       ArrayList<ArrayList<DocDetails>> clusters = initializeClusters(documents, k);
       
       ArrayList<DocDetails> centroids = calculateInitialCentroids(clusters);

       boolean converged = false;
        
        /** Calculo de los cluster.
         * Se calcula nuevos clusters a partir de las distancias de los centroids a los
         * documentos y se compara con los clusters actuales, en caso de ser diferentes
         * se actualiza el listado de clusters, se calculan nuevos centroids y se sigue 
         * el proceso. En caso contrario converge y se devuelven los clusters
         */
        while (!converged) {       
           // Asignar cada documento al grupo más cercano           
           ArrayList<ArrayList<DocDetails>> newClusters = new ArrayList<ArrayList<DocDetails>>();
            for (int i = 0; i < k; i++) {
                ArrayList<DocDetails> x =  new ArrayList<DocDetails>();
                newClusters.add(x);
            }
            /** Calculo de las distancias para determinar el mejor cluster para
             * cada documento. 
             * Se recorre la lista de documentos y se calcula la
             * distancia mínima de cada documento con respecto a los documentos que son 
             * centroid de los clusters. El cluster que de la menor distancia 
             * es donde se guarda el documento. El calculo de la distancia se hace
             * a partir de los tokens del documento.
             */            
            for (DocDetails doc : documents) {
                double minDistance = Double.MAX_VALUE;
                int closestCluster = -1;
                for (int i = 0; i < k; i++) {
                    double distance = calculateDistance(doc.getToken(), centroids.get(i).getToken());
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestCluster = i;
                    }
                }
                newClusters.get(closestCluster).add(doc);
            }

            /**
             * Verificar si los grupos han convergido.
             * Converge si se obtiene el mismo agrupamiento en clusters en dos 
             * iteraciones consecutivas. En la primera iteracion se compara con
             * el conjunto de cluster aleatrios-
             */
            if (clusters.equals(newClusters)) {
                converged = true;
            } else {
                clusters = newClusters;
                centroids = calculateCentroids(clusters);
            }
        }

        return clusters;
    }
    
    /**
     * Este método inicializa k cluster de documentos.
     * Crea una lista de listas de documentos, cada una de esas listas es un 
     * cluster, se crean k cluster, luego ubica de forma equitativa los documentos
     * en los cluster
     * @param documents listado de documentos
     * @param k cantidad de cluster
     * @return 
     */
    private static ArrayList<ArrayList<DocDetails>> initializeClusters(ArrayList<DocDetails> documents, int k) {
       ArrayList<ArrayList<DocDetails>> clusters = new ArrayList<ArrayList<DocDetails>>();
       //Inicializacion de los cluster con las listas de DocDetails 
       for (int i = 0; i < k; i++) {
            ArrayList<DocDetails> x =  new ArrayList<DocDetails>();
            clusters.add(x);
        }
       
        for (int i = 0; i < documents.size(); i++) {
            DocDetails doc = documents.get(i);
            int clusterIndex = i % k; // este calculo se hace para repartir de 
                                      //forma equitativa los documentos en el cluster
            clusters.get(clusterIndex).add(doc);
        }
        return clusters;
    }
    
     /** Calcula los documentos que serán centroids de cada cluster.
     * A partir de la lista de clusters se elige al primer documento de cada cluster como el centroid
     * y se devuelve dicha lista
     * @param clusters
     * @return Listado de documentos que son centroids
     */
    private static ArrayList<DocDetails> calculateInitialCentroids(ArrayList<ArrayList<DocDetails>> clusters) {
       ArrayList<DocDetails> centroids = new ArrayList<>();
        for (ArrayList<DocDetails> cluster : clusters) {
            DocDetails centroid = cluster.get(0);
            centroids.add(centroid);
        }
        return centroids;
    }

    /**
     * Calculo de los nuevos centroids.
     * A partir de cada cluster se crea un nuevo centroid con los tokens de todos 
     * los documentos del cluster
     * @param clusters
     * @return 
     */
    private static ArrayList<DocDetails> calculateCentroids(ArrayList<ArrayList<DocDetails>> clusters) {
       ArrayList<DocDetails> centroids = new ArrayList<>();
        for (ArrayList<DocDetails> cluster : clusters) {
            DocDetails centroid = calculateCentroid(cluster);
            centroids.add(centroid);
        }
        return centroids;
    }
    
    /**
     * Calcula un centroid.
     * Este método a partir de un cluster crea un documento llamado "Centroid"
     * y le agrega un listado de tokens compuesto por todos los tokens de los 
     * documentos del cluster
     * @param cluster
     * @return 
     */
    private static DocDetails calculateCentroid(ArrayList<DocDetails> cluster) {
       String nombre = "Centroid";
       ArrayList<String> tokens = new ArrayList<>();
        int numDocuments = cluster.size();
        for (DocDetails doc : cluster) {
            for (String token : doc.getToken()) {
                tokens.add(token);
            }
        }
        return new DocDetails(nombre, tokens);
    }
   
    /** Calcula la distancia entre documentos a partir de la pertenencia o no de tokens.
     * A partir de un documento se le calcula la distancia en relación a otro documento 
     * utilizando los tokens de ambos. Se pregunta si en la lista de tokens de uno se
     * encuentra los tokens del otro, por cada token que contenga se suma 1 y luego esta
     * suma se divide entre 1, siendo esta la distancia, evidentemente mientas más tokens
     * coincidan menor es la distancia
     * @param tokensdoc tokens del documento
     * @param tokenscent tokens del documento centroid
     * @return 
     */
    private static double calculateDistance(ArrayList<String> tokensdoc,ArrayList<String> tokenscent) {
        double x = 0.0;
        for(int i=0;i<tokenscent.size();i++){
            if(tokensdoc.contains(tokenscent.get(i)))x++;
        }
        if(x==0){
            return 1;
        }else{
            return 1.0/x; 
        }        
    }
}
