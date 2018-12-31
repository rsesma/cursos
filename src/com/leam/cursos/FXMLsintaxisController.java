/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leam.cursos;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.leam.cursos.Pregunta.TipoPregunta;
import com.leam.cursos.Sintaxis.TipoSintaxis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author R
 */
public class FXMLsintaxisController implements Initializable {
    @FXML
    private TextField cd;
    @FXML
    private ToggleGroup Curso;
    @FXML
    private RadioButton st1;
    @FXML
    private RadioButton st2;
    @FXML
    private TextField total;
    @FXML
    private TextField preguntas;
    @FXML
    private TextField excluir;
    @FXML
    private TextField sheet;
    @FXML
    private TextField erase;
    
    @FXML
    private CheckBox p1;
    @FXML
    private TextField n1;
    @FXML
    private ToggleGroup tipo1;
    @FXML
    private RadioButton test1;
    @FXML
    private RadioButton import1;
    @FXML
    private TextField dta1;
    @FXML
    private TextField vars1;
    @FXML
    private TextField pregs1;
    @FXML
    private TextField id1;

    @FXML
    private CheckBox p2;
    @FXML
    private TextField n2;
    @FXML
    private ToggleGroup tipo2;
    @FXML
    private RadioButton test2;
    @FXML
    private RadioButton import2;
    @FXML
    private TextField dta2;
    @FXML
    private TextField vars2;
    @FXML
    private TextField pregs2;
    @FXML
    private TextField id2;
	
    @FXML
    private CheckBox p3;
    @FXML
    private TextField n3;
    @FXML
    private ToggleGroup tipo3;
    @FXML
    private RadioButton test3;
    @FXML
    private RadioButton import3;
    @FXML
    private TextField dta3;
    @FXML
    private TextField vars3;
    @FXML
    private TextField pregs3;
    @FXML
    private TextField id3;
    
    @FXML
    private CheckBox p4;
    @FXML
    private TextField n4;
    @FXML
    private ToggleGroup tipo4;
    @FXML
    private RadioButton test4;
    @FXML
    private RadioButton import4;
    @FXML
    private TextField dta4;
    @FXML
    private TextField vars4;
    @FXML
    private TextField pregs4;
    @FXML
    private TextField id4;

    @FXML
    private CheckBox p5;
    @FXML
    private TextField n5;
    @FXML
    private ToggleGroup tipo5;
    @FXML
    private RadioButton test5;
    @FXML
    private RadioButton import5;
    @FXML
    private TextField dta5;
    @FXML
    private TextField vars5;
    @FXML
    private TextField pregs5;
    @FXML
    private TextField id5;

    File def = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.sheet.setText("Datos");
        
        this.st1.setUserData(TipoSintaxis.ST1);
        this.st2.setUserData(TipoSintaxis.ST2);
        
        this.test1.setUserData(TipoPregunta.TEST);
        this.import1.setUserData(TipoPregunta.IMPORT);
        this.test2.setUserData(TipoPregunta.TEST);
        this.import2.setUserData(TipoPregunta.IMPORT);
        this.test3.setUserData(TipoPregunta.TEST);
        this.import3.setUserData(TipoPregunta.IMPORT);
        this.test4.setUserData(TipoPregunta.TEST);
        this.import4.setUserData(TipoPregunta.IMPORT);
        this.test5.setUserData(TipoPregunta.TEST);
        this.import5.setUserData(TipoPregunta.IMPORT);
    }

    public void SetData(File dir, TipoSintaxis type) {
    	if (type == TipoSintaxis.ST1) {
    		def = new File(dir,"ST1");
    		this.st1.selectedProperty().set(true);
    	}
    	if (type == TipoSintaxis.ST2) {
    		def = new File(dir,"ST2");
    		this.st2.selectedProperty().set(true);
    	}
        this.cd.setText(def.getAbsolutePath());        
    }
    
    @FXML
    void pbDefaultDir(ActionEvent event) {
        // get default folder
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Escoger carpeta por defecto");
        chooser.setInitialDirectory(this.def);
        File dir = chooser.showDialog(null);
        if (dir != null) this.cd.setText(dir.getAbsolutePath());
    }
    
    @FXML
    void pbExportar(ActionEvent event) {
        // build Sintaxis object to export
        Sintaxis S = new Sintaxis((TipoSintaxis) this.Curso.getSelectedToggle().getUserData(), 
                Integer.parseInt(this.total.getText()),
                Integer.parseInt(this.preguntas.getText()),
                this.excluir.getText());
        S.SetDir(this.cd.getText());
        S.SetSheet(this.sheet.getText());
        S.SetErase(this.erase.getText());
        
        // add defined preguntas
        if (this.p1.isSelected()) S.addPregunta(0, new Pregunta(Integer.parseInt(this.n1.getText()), 
                (TipoPregunta) this.tipo1.getSelectedToggle().getUserData(), 
                this.dta1.getText(), this.vars1.getText(), this.pregs1.getText(), this.id1.getText()));

        if (this.p2.isSelected()) S.addPregunta(1, new Pregunta(Integer.parseInt(this.n2.getText()), 
                (TipoPregunta) this.tipo2.getSelectedToggle().getUserData(), 
                this.dta2.getText(), this.vars2.getText(), this.pregs2.getText(), this.id2.getText()));

        if (this.p3.isSelected()) S.addPregunta(2, new Pregunta(Integer.parseInt(this.n3.getText()), 
                (TipoPregunta) this.tipo3.getSelectedToggle().getUserData(), 
                this.dta3.getText(), this.vars3.getText(), this.pregs3.getText(), this.id3.getText()));
        
        if (this.p4.isSelected()) S.addPregunta(3, new Pregunta(Integer.parseInt(this.n4.getText()), 
                (TipoPregunta) this.tipo4.getSelectedToggle().getUserData(), 
                this.dta4.getText(), this.vars4.getText(), this.pregs4.getText(), this.id4.getText()));
        
        if (this.p5.isSelected()) S.addPregunta(4, new Pregunta(Integer.parseInt(this.n5.getText()), 
                (TipoPregunta) this.tipo5.getSelectedToggle().getUserData(), 
                this.dta5.getText(), this.vars5.getText(), this.pregs5.getText(), this.id5.getText()));
        
        // export sintaxis file
        S.Exportar();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Proceso finalizado");
        alert.showAndWait();

        Stage stage = (Stage) this.cd.getScene().getWindow();
        stage.close();
    }
}
