/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistrecuperacioninformacion.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sistrecuperacioninformacion.Cluster;
import sistrecuperacioninformacion.DocDetails;
import sistrecuperacioninformacion.FuzzyCMeans;
import sistrecuperacioninformacion.Kmeans;
import sistrecuperacioninformacion.Linkage;
import sistrecuperacioninformacion.Main;
import sistrecuperacioninformacion.TikaLuceneProcessing;

/**
 *
 * @author Ale
 */
public class PrincipalController implements Initializable {

    @FXML
    private Pane paneContenedor;
    @FXML
    private Pane paneBarraTitulo;
    @FXML
    private TextArea JFXTextAreaGroups;
    @FXML
    private TextArea JFXTextAreaDocuments;
    @FXML
    private Pane JFXPaneContenedorCarga;

    //Mis atributoss
    public static Pane paneGlobal;
    ArrayList<DocDetails> documents; // Documentos cargados

    //make dragable (permitir que la ventana se arraste)
    private double xOffSet = 0;
    private double yOffSet = 0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paneGlobal = paneContenedor;
        makeStageDragable();
    }

    /**
     * Metodo que permite al Stage ser transparente y que pueda moverse cuando
     * su estilo es UNDECORATED.
     */
    private void makeStageDragable() {
        //Para el paneContenedor global y para el paneBarraTitulo se le establece la propiedad
        //PaneGlobal
        paneGlobal.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        paneGlobal.setOnMouseDragged((event) -> {
            Main.stage.setX(event.getScreenX() - xOffSet);
            Main.stage.setY(event.getScreenY() - yOffSet);
            Main.stage.setOpacity(0.8f);
        });
        paneGlobal.setOnDragDone((event) -> {
            Main.stage.setOpacity(1.0f);
        });
        paneGlobal.setOnMouseReleased((event) -> {
            Main.stage.setOpacity(1.0f);
        });

        //PaneBarraTitulo
        paneBarraTitulo.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        paneBarraTitulo.setOnMouseDragged((event) -> {
            Main.stage.setX(event.getScreenX() - xOffSet);
            Main.stage.setY(event.getScreenY() - yOffSet);
            Main.stage.setOpacity(0.8f);
        });
        paneBarraTitulo.setOnDragDone((event) -> {
            Main.stage.setOpacity(1.0f);
        });
        paneBarraTitulo.setOnMouseReleased((event) -> {
            Main.stage.setOpacity(1.0f);
        });
    }

    @FXML
    private void cerrar(MouseEvent event) {
        System.exit(0);

    }

    @FXML
    private void minimizar(MouseEvent event) {
        /*Codigo para minimizar*/
        Stage stage = (Stage) paneGlobal.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    // Cargar los DocDetails
    private void cargar(MouseEvent event) {
        JFXTextAreaDocuments.clear();
        JFXTextAreaGroups.clear();
        documents = null;
        try {
            // Crear un nuevo DirectoryChooser
            DirectoryChooser directoryChooser = new DirectoryChooser();
            // Configurar el título del cuadro de diálogo
            directoryChooser.setTitle("Seleccionar carpeta");
            // Mostrar el cuadro de diálogo y esperar a que el usuario seleccione una carpeta
            File selectedDirectory = directoryChooser.showDialog(Main.stage);

            if (selectedDirectory != null) {
                //Cargar la vista Loading
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistrecuperacioninformacion/view/Loading.fxml"));
                Node node = loader.load();
                JFXPaneContenedorCarga.getChildren().clear();
                JFXPaneContenedorCarga.getChildren().add(node);

                //Procesar el directorio seleccionado en un hilo aparte
                Thread thread = new Thread(() -> {
                    try {
                        // Cargar los documentos
                        documents = TikaLuceneProcessing.procesarDocs(new File(selectedDirectory.getAbsolutePath()));
                        // Una vez que el trabajo haya terminado, ejecutar ciertas acciones en el hilo de JavaFX
                        Platform.runLater(() -> {
                            //Quitar el loading
                            JFXPaneContenedorCarga.getChildren().clear();
                            JFXPaneContenedorCarga.getChildren().add(JFXTextAreaDocuments);
                            //Cargar los nombres de ficheros y ponerlos en el textarea del visual
                            for (int i = 0; i < documents.size(); i++) {
                                JFXTextAreaDocuments.appendText(documents.get(i).getNombre() + "\n");
                            }
                        });
                    } catch (IOException ex) {
                        Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                // Iniciar el hilo que carga los documentos
                thread.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void limpiar(MouseEvent event) {
        JFXTextAreaDocuments.clear();
        JFXTextAreaGroups.clear();
        this.documents = null;
    }

    @FXML
    private void kmeans(MouseEvent event) {
        if (this.documents != null) {
            JFXTextAreaGroups.clear();
            ArrayList<ArrayList<DocDetails>> clusters = Kmeans.kmeans(documents, 3);
            String solution = "K-Means:\n";
            for (int i = 0; i < clusters.size(); i++) {
                solution += "Grupo " + (i + 1) + ":\n";
                for (int j = 0; j < clusters.get(i).size(); j++) {
                    solution += "Documento: " + clusters.get(i).get(j).getNombre() + "\n";
                }
                solution += "\n";
            }
            // Aunque K sea 3 puede que solo se impriman 2 o menos grupos pq pueden que algunos
            // se queden vacios cuando se actualicen los centroids y estos coincidan
            JFXTextAreaGroups.setText(solution);
        } else {
            mensaje();
        }
    }

    @FXML
    private void linkage(MouseEvent event) {
        if (this.documents != null) {
            JFXTextAreaGroups.clear();
            double[][] distanceMatrix = Linkage.calculateDistanceMatrix(documents);
            // Realizar el clustering jerárquico aglomerativo utilizando Linkage
            ArrayList<Cluster> clusters_link = Linkage.performLinkageClustering(documents, distanceMatrix);
            // Imprimir los resultados del clustering (Clustering Jerárquico Aglomerativo)
            String solution = "Linkage:\n";
            for (int i = 0; i < clusters_link.size(); i++) {
                solution += "Grupo " + (i + 1) + ":\n";
                ArrayList<Integer> clusterIndices = clusters_link.get(i).getIndices();
                solution += clusterIndices + "\n";
                for (int index : clusterIndices) {
                    DocDetails doc = documents.get(index);
                    solution += "Nombre del documento: " + doc.getNombre() + "\n";
                }
                solution += "\n";
            }
            JFXTextAreaGroups.setText(solution);
        } else {
            mensaje();
        }
    }

    @FXML
    private void fuzzy(MouseEvent event) {
        if (this.documents != null) {
            JFXTextAreaGroups.clear();
            double[][] dataMatrix = FuzzyCMeans.getDataMatrix(documents);
            // Ejecutar Fuzzy C-Means
            double[][] centroids = FuzzyCMeans.fuzzyCMeansClustering(dataMatrix, FuzzyCMeans.NUM_CLUSTERS, FuzzyCMeans.FUZZINESS, FuzzyCMeans.EPSILON, FuzzyCMeans.MAX_ITERATIONS);
            // Asignar documentos a los grupos correspondientes
            int[] clusterAssignments = FuzzyCMeans.assignDocumentsToClusters(centroids);
            // Imprimir los resultados
            String solution = "Fuzzy C-means:\n";
            for (int i = 0; i < documents.size(); i++) {
                DocDetails doc = documents.get(i);
                int cluster = clusterAssignments[i];
                solution += "Documento: " + doc.getNombre() + ", Cluster: " + cluster + " con pertenencia: " + centroids[i][cluster] + "\n";
            }
            solution += "\n";
            JFXTextAreaGroups.setText(solution);
        } else {
            mensaje();
        }
    }

    private void mensaje() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText("Debe cargar los documentos");
        alert.showAndWait();
    }
}
