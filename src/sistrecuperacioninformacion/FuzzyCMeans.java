/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistrecuperacioninformacion;

import java.util.ArrayList;

/**
 *
 * @author Aida Rosa
 */
public class FuzzyCMeans {

    public static final int NUM_CLUSTERS = 2;
    public static final double FUZZINESS = 2.0;
    public static final int MAX_ITERATIONS = 100;
    public static final double EPSILON = 0.001;

    /**
     * Recibe una lista de doctails y devuelve una matriz de tokens donde cada
     * fila es un documento y las columnas son tokens asociados a ese documento.
     * Es importante notar que en cada posicion de la matriz lo que se tiene es
     * el valor numerico del token obtenido a partir de metodo hashCode(). Esto
     * se hace para calculos posteriores
     *
     * @param documents
     * @return
     */
    public static double[][] getDataMatrix(ArrayList<DocumentDetails> documents) {
        int numDocuments = documents.size();
        // extraer la máxima cantidad de tokens entre todo el conjuntod de ficheros
        int maxTokens = getMaxTokens(documents);

        /**
         * Establece una matriz con una cantidad de filas igual a la cantidad de
         * documentos y una cantidad de columnas igual al maximo numero de
         * tokens entre el conjunto de tokens de cada documento.
         */
        double[][] dataMatrix = new double[numDocuments][maxTokens];

        /**
         * Crear la matriz donde en cada posicion i (documento) se ponen los j
         * tokens correspondientes a ese documento.
         */
        for (int i = 0; i < numDocuments; i++) {
            DocumentDetails doc = documents.get(i);
            ArrayList<String> tokens = doc.getToken();

            for (int j = 0; j < tokens.size(); j++) {
                // Utilizar un valor numérico para representar los tokens
                dataMatrix[i][j] = Math.abs(tokens.get(j).hashCode());
            }
        }

        return dataMatrix;
    }

    /**
     * Recube una lista de documentos y busca entre todos el que más tokens
     * tiene y devuelve esa cantidad.
     *
     * @param documents
     * @return maxima cantidad de tokens entre todos los documentos
     */
    private static int getMaxTokens(ArrayList<DocumentDetails> documents) {
        int maxTokens = 0;
        for (DocumentDetails doc : documents) {
            int tokensSize = doc.getToken().size();
            if (tokensSize > maxTokens) {
                maxTokens = tokensSize;
            }
        }
        return maxTokens;
    }

    /**
     * Devuelve la matriz de pertenencia. Este método a partir de una matriz de
     * documentos, un factor difuso m, un numero de cluster, un valor de
     * toleracia epsilon y un máximo número de iteraciones calcula la matriz de
     * pertenencia donde se refleja el grado de pertenencia de cada documento a
     * cada cluster.
     *
     * @param dataMatrix
     * @param numClusters
     * @param m
     * @param epsilon
     * @param maxIterations
     * @return
     */
    public static double[][] fuzzyCMeansClustering(double[][] dataMatrix, int numClusters, double m, double epsilon, int maxIterations) {
        int numDocuments = dataMatrix.length;
        int numTokens = dataMatrix[0].length;

        double[][] membershipMatrix = initializeMembershipMatrix(numDocuments, numClusters);

        double[][] centroids = initializeCentroids(dataMatrix, numClusters, numTokens);

        double[][] previousMembershipMatrix = new double[numDocuments][numClusters];

        double delta;
        int iteration = 0;
        do {
            copyMembershipMatrix(membershipMatrix, previousMembershipMatrix);

            updateCentroids(dataMatrix, membershipMatrix, centroids, m);

            updateMembershipMatrix(dataMatrix, membershipMatrix, centroids, m);

            delta = calculateDelta(membershipMatrix, previousMembershipMatrix);

            iteration++;

            /**
             * Se comprueba si el delta es menor que el nivel de
             * tolerancia(epsilon) definido y si se ha alcanzado el máximo
             * número de iteraciones, en caso de ser true cualquiera se termina
             * el ciclo, si ambas son falsas se continua para la proxima
             * iteracion
             */
        } while (delta > epsilon && iteration < maxIterations);

        return membershipMatrix;
    }

    /**
     * Define la matriz de pertenencia con valores random. A partir de la
     * cantidad de documentos y la cantidad de cluster(grupos) crea la matriz de
     * pertenencia con valores random utilizando Math.
     *
     * @param numDocuments
     * @param numClusters
     * @return
     */
    private static double[][] initializeMembershipMatrix(int numDocuments, int numClusters) {
        double[][] membershipMatrix = new double[numDocuments][numClusters];
        for (int i = 0; i < numDocuments; i++) {
            for (int j = 0; j < numClusters; j++) {
                membershipMatrix[i][j] = Math.random();
            }
        }
        normalizeMembershipMatrix(membershipMatrix); //normalizar la matriz
        return membershipMatrix;
    }

    /**
     * Se normaliza la matriz de pertenencia. Se toma la matriz de pertenencia
     * inicializada con valores aleatorios y se le aplica normalizacion del tipo
     * Uij / SUM Uij desde i=1 hasta c, siendo c el máximo número de cluster,
     * para toda j=1,2,..N. Esto se hace para garantizar que la suma de la
     * pertenencia de un documento a todos los cluster sea igual a 1. En este
     * caso como se trabaja con double sería un número muy cercano Ex:
     * 0.999999417225
     *
     * @param membershipMatrix
     */
    private static void normalizeMembershipMatrix(double[][] membershipMatrix) {
        int numDocuments = membershipMatrix.length;
        int numClusters = membershipMatrix[0].length;
        for (int i = 0; i < numDocuments; i++) {
            double sum = 0.0;
            for (int j = 0; j < numClusters; j++) {
                sum += membershipMatrix[i][j];
            }
            for (int j = 0; j < numClusters; j++) {
                membershipMatrix[i][j] /= sum;
            }
        }
    }

    /**
     * Se inicializan los centroids. Esto van a ser una matriz de numCluster X
     * numTokens similar a la matriz de datos (dataMatriz) pero con la
     * diferencia de que al inicio los cluster van a ser iguales a los k
     * documentos, donde k es el numCluster y en cada posicion de la matriz
     * estarán los tokens correspondientes
     *
     * @param dataMatrix
     * @param numClusters
     * @param numTokens
     * @return
     */
    private static double[][] initializeCentroids(double[][] dataMatrix, int numClusters, int numTokens) {
        double[][] centroids = new double[numClusters][numTokens];
        for (int i = 0; i < numClusters; i++) {
            for (int j = 0; j < numTokens; j++) {
                centroids[i][j] = dataMatrix[i][j];
            }
        }
        return centroids;
    }

    /**
     * Actualiza los centroides utilizando una fórmula. La actualizacion de
     * centroides se inspira en la fórmula ubicada en el sitio:
     * https://openaccess.uoc.edu/bitstream/10609/59066/7/ruizjcTFG0117memoria.pdf
     * Pero la matriz de pertenencia utilizada en ese sitio es la traspuesta en
     * este algoritmo
     *
     * @param dataMatrix
     * @param membershipMatrix
     * @param centroids
     * @param m
     */
    private static void updateCentroids(double[][] dataMatrix, double[][] membershipMatrix, double[][] centroids, double m) {
        int numClusters = centroids.length;
        int numTokens = centroids[0].length;
        for (int j = 0; j < numClusters; j++) {
            for (int k = 0; k < numTokens; k++) {
                double numerator = 0.0;
                double denominator = 0.0;
                for (int i = 0; i < dataMatrix.length; i++) {
                    double membership = Math.pow(membershipMatrix[i][j], m);
                    numerator += membership * dataMatrix[i][k];
                    denominator += membership;
                }
                centroids[j][k] = numerator / denominator;
            }
        }
    }

    /**
     * Actualizar el grado de pertenencia del documento i al cluster j. Para
     * cada documento i de la matriz dataMatrix se aplica el proceso de calcular
     * el grado de pertenencia de este documento al cluster j, por ejemplo si
     * hay 2 clusters y 4 documentos se analiza para cada documento su grado de
     * pertenencia al cluster 1 y luego al cluster 2. Este grado de pertenencia
     * se pone en una matriz de pertenencia la cual luego se normaliza, para
     * asegurar que la suma de los valores de pertenencia de cada documento de
     * un número muy cercano al 1 o igual a 1.
     *
     * @param dataMatrix
     * @param membershipMatrix
     * @param centroids
     * @param m
     */
    private static void updateMembershipMatrix(double[][] dataMatrix, double[][] membershipMatrix, double[][] centroids, double m) {
        int numDocuments = membershipMatrix.length;
        int numClusters = membershipMatrix[0].length;
        for (int i = 0; i < numDocuments; i++) {
            for (int j = 0; j < numClusters; j++) {
                double membership = calculateMembership(dataMatrix[i], centroids[j], centroids, m);
                membershipMatrix[i][j] = membership;
            }
        }
        normalizeMembershipMatrix(membershipMatrix);
    }

    /**
     * A partir de un documento k recibido calcula su grado de pertenencia al
     * cluster i. Se recibe un documento(double[] document), el cluster al que
     * se le calcula el grado de pertenencia, también se recibe todo el conjunto
     * y un valor m llamado factor difuso que indica cuanto queremos que se
     * solapen los grupos. Se aplica la formula ubicada en la ruta:
     * https://openaccess.uoc.edu/bitstream/10609/59066/7/ruizjcTFG0117memoria.pdf
     * En esta fórmula lo que se hace es hallar la distancia del documento al
     * cluster i(Dki) y luego se divide esta por la distancia del documento a
     * cada uno de los cluster (Dkj). En cada paso se aplica P = Dki/Dkj, a esto
     * se le aplica pow(P,2/(m-1)) y se va sumando para cada j-cluster,
     * finalmente esa suma(S) se eleva a -1 y queda que Uki = 1/S.
     *
     * @param document
     * @param cluster
     * @param centroids
     * @param m
     * @return
     */
    private static double calculateMembership(double[] document, double[] cluster, double[][] centroids, double m) {
        double numerator = calculateDistance(document, cluster);
        double denominator = 0.0;
        for (double[] otherCluster : centroids) {
            double distance = calculateDistance(document, otherCluster);
            denominator += Math.pow(numerator / distance, 2.0 / (m - 1.0));
        }
        return 1.0 / denominator;
    }

    /**
     * Se calcula la distancia euclidiana entre el vector documento y el cluster
     * La distancia euclidiana es: Se tiene dos vectores X y Y en R2, la
     * distancia entre ellos es d(X,Y) = sqrt( pow((x1 - y1),2) + pow((x2 -
     * y2),2) + )
     *
     * @param document
     * @param cluster
     * @return
     */
    private static double calculateDistance(double[] document, double[] cluster) {
        double sum = 0.0;
        for (int i = 0; i < document.length; i++) {
            double diff = document[i] - cluster[i];
            sum += Math.pow(diff, 2.0);
        }
        return Math.sqrt(sum);
    }

    /**
     * Este método copia el contenido de una matriz a otra
     *
     * @param source
     * @param destination
     */
    private static void copyMembershipMatrix(double[][] source, double[][] destination) {
        int numDocuments = source.length;
        int numClusters = source[0].length;
        for (int i = 0; i < numDocuments; i++) {
            System.arraycopy(source[i], 0, destination[i], 0, numClusters);
        }
    }

    /**
     * Calcula un valor delta que sirve como condicion de parada. Este valor
     * delta lo que hace es sumar la resta absoluta de los valores de las
     * matrices de pertenencia de dos iteraciones consecutivas para comprobar
     * que tan diferentes son sus resultados, el valor de esta suma obtenida es
     * el delta que se devuelve.
     *
     * @param membershipMatrix
     * @param previousMembershipMatrix
     * @return
     */
    private static double calculateDelta(double[][] membershipMatrix, double[][] previousMembershipMatrix) {
        double sum = 0.0;
        int numDocuments = membershipMatrix.length;
        int numClusters = membershipMatrix[0].length;
        for (int i = 0; i < numDocuments; i++) {
            for (int j = 0; j < numClusters; j++) {
                sum += Math.abs(membershipMatrix[i][j] - previousMembershipMatrix[i][j]);
            }
        }
        return sum;
    }

    /**
     * Asigna a cada documento el mejor cluster a partir de su grado de
     * pertenencia. Este método recibe una matriz de pertenecia y se encarga de
     * buscar para cada documento el mejor cluster, es decir, el de mayor grado
     * de pertenencia. Finalemente devuelve un vector donde cada posicion hace
     * referencia al documento y en cada posicion se guarda el cluster asignado
     * a ese documento.
     *
     * @param membershipMatrix
     * @return
     */
    private static int[] assignDocumentsToClusters(double[][] membershipMatrix) {
        int numDocuments = membershipMatrix.length;
        int numClusters = membershipMatrix[0].length;
        int[] clusterAssignments = new int[numDocuments];
        for (int i = 0; i < numDocuments; i++) {
            int bestCluster = 0;
            double bestMembership = membershipMatrix[i][0];
            for (int j = 1; j < numClusters; j++) {
                if (membershipMatrix[i][j] > bestMembership) {
                    bestCluster = j;
                    bestMembership = membershipMatrix[i][j];
                }
            }
            clusterAssignments[i] = bestCluster;
        }
        return clusterAssignments;
    }

    /**
     * A partir de una matriz de pertenecia y un listado de documentos devuelve
     * un listado de clusters. Este método crea un listado de cluster
     * auxiliándose de assignDocumentsToClusters
     *
     * @param membershipMatrix
     * @param documents
     * @return
     */
    public static ArrayList<ArrayList<DocumentDetails>> getClusters(double[][] membershipMatrix,
            ArrayList<DocumentDetails> documents) {
        //Cargar las asignaciones
        int[] clusterAssignments = assignDocumentsToClusters(membershipMatrix);
        //Listado de cluster
        ArrayList<ArrayList<DocumentDetails>> clusters = new ArrayList<>();
        for (int i = 0; i < membershipMatrix[0].length; i++) {
            clusters.add(new ArrayList());
        }
        //Cargar los cluster
        for (int i = 0; i < documents.size(); i++) {
            String nombre = documents.get(i).getNombre();

            ArrayList<String> tokens = documents.get(i).getToken();

            double significacion = membershipMatrix[i][clusterAssignments[i]];

            DocumentDetails document = new DocumentDetails(nombre, tokens, significacion);

            clusters.get(clusterAssignments[i]).add(document);
        }
        return clusters;
    }

    /*public static void imprimir(double[][] matrix) {
        String content = "";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                double number = matrix[i][j];
                content += number + " ";
            }
            content += "\n";
        }
        System.out.println(content);
    }*/
}
