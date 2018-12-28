package com.leam.cursos;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Cursos extends Application {
    
	@Override
    public void start(Stage stage) throws Exception {

		GetData d = null;
		
        // Launch login window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLlogin.fxml")); 
        Parent r0 = (Parent) loader.load();
        Stage stage0 = new Stage(); 
        stage0.initModality(Modality.WINDOW_MODAL);
        stage0.setTitle("Registro");
        stage0.setScene(new Scene(r0));
        FXMLloginController login = loader.<FXMLloginController>getController();
        stage0.showAndWait();
        
        Boolean ok = login.ok;
        
        if (ok) {
        	d = login.d;
            try{
                ResultSet rs = d.getNotas();
                while(rs.next()){
                	System.out.println(rs.getString("Cod")+"   "+rs.getString("Descrip"));
                }
            } catch(SQLException e){
                System.out.println(e.getMessage());
            }

        }
	}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}