package com.leam.cursos;

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
		
		Boolean test = false;
		Boolean ok = false;
		
		if (!test) {
	        // Launch login window
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLlogin.fxml")); 
	        Parent r0 = (Parent) loader.load();
	        Stage stage0 = new Stage(); 
	        stage0.initModality(Modality.WINDOW_MODAL);
	        stage0.setTitle("Registro");
	        stage0.setScene(new Scene(r0));
	        FXMLloginController login = loader.<FXMLloginController>getController();
	        stage0.showAndWait();
	        
	        ok = login.ok;
	        d = login.d;
		} else {
			ok = true;
	        d = new GetData();
	        d.getConnection("rsesma","Amsesr.2108","192.168.1.10");
	    }	    
        
        if (ok) {
        	
            if (ok) {
                FXMLLoader fxml = new FXMLLoader(getClass().getResource("DlgInit.fxml"));
                Parent root = (Parent) fxml.load();
                DlgInitController dlg = fxml.<DlgInitController>getController();
                dlg.SetData(d);

                Scene scene = new Scene(root);
                stage.setTitle("Operativa");
                stage.setScene(scene);
                stage.show();
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