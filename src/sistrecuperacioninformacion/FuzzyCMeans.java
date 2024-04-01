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


   public static double[][] getDataMatrix(ArrayList<DocDetails> documents) {
        int numDocuments = documents.size();
        int maxTokens = getMaxTokens(documents);

        double[][] dataMatrix = new double[numDocuments][maxTokens];

        for (int i = 0; i < numDocuments; i++) {
            DocDetails doc = documents.get(i);
            ArrayList<String> tokens = doc.getToken();

            for (int j = 0; j < tokens.size(); j++) {
                // Utilizar un valor numérico para representar los tokens
                dataMatrix[i][j] = tokens.get(j).hashCode();
            }
        }

        return dataMatrix;
    }

    private static int getMaxTokens(ArrayList<DocDetails> documents) {
        int maxTokens = 0;
        for (DocDetails doc : documents) {
            int tokensSize = doc.getToken().size();
            if (tokensSize > maxTokens) {
                maxTokens = tokensSize;
            }
        }
        return maxTokens;
    }

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
        } while (delta > epsilon && iteration < maxIterations);

        return membershipMatrix;
    }

    private static double[][] initializeMembershipMatrix(int numDocuments, int numClusters) {
        double[][] membershipMatrix = new double[numDocuments][numClusters];
        for (int i = 0; i < numDocuments; i++) {
            for (int j = 0; j < numClusters; j++) {
                membershipMatrix[i][j] = Math.random();
            }
        }
        normalizeMembershipMatrix(membershipMatrix);
        return membershipMatrix;
    }

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

    private static double[][] initializeCentroids(double[][] dataMatrix, int numClusters, int numTokens) {
        double[][] centroids = new double[numClusters][numTokens];
        for (int i = 0; i < numClusters; i++) {
            for (int j = 0; j < numTokens; j++) {
                centroids[i][j] = dataMatrix[i][j];
            }
        }
        return centroids;
    }

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

    private static double calculateMembership(double[] document, double[] cluster, double[][] centroids, double m) {
        double numerator = calculateDistance(document, cluster);
        double denominator = 0.0;
        for (double[] otherCluster : centroids) {
            double distance = calculateDistance(document, otherCluster);
            denominator += Math.pow(numerator / distance, 2.0 / (m - 1.0));
        }
        return 1.0 / denominator;
    }

    private static double calculateDistance(double[] document, double[] cluster) {
        double sum = 0.0;
        for (int i = 0; i < document.length; i++) {
            double diff = document[i] - cluster[i];
            sum += Math.pow(diff, 2.0);
        }
        return Math.sqrt(sum);
    }

    private static void copyMembershipMatrix(double[][] source, double[][] destination) {
        int numDocuments = source.length;
        int numClusters = source[0].length;
        for (int i = 0; i < numDocuments; i++) {
            System.arraycopy(source[i], 0, destination[i], 0, numClusters);
        }
    }

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

    public static int[] assignDocumentsToClusters(double[][] membershipMatrix) {
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

}
