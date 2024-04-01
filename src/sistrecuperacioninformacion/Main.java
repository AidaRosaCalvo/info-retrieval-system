/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistrecuperacioninformacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author Aida Rosa
 */
public class Main extends Application {
    
    public static Stage stage = null;
    
    @Override
    public void start(Stage stage) throws Exception {
        //BasicConfigurator.configure();
        Parent root = FXMLLoader.load(getClass().getResource("view/Principal.fxml"));
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setScene(scene);        
        Main.stage = stage;
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
    }
    
}
