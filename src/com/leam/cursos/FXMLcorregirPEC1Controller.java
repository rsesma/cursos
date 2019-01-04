/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leam.cursos;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author r
 */
public class FXMLcorregirPEC1Controller implements Initializable {

    @FXML
    private TableView<Alumno> table;
    @FXML
    private TableColumn<Alumno,String> nCol;
    @FXML
    private TableColumn<Alumno,String> periodoCol;
    @FXML
    private TableColumn<Alumno,String> grupoCol;
    @FXML
    private TableColumn<Alumno,String> DNICol;
    @FXML
    private TableColumn<Alumno,String> PCCol;
    @FXML
    private TableColumn<Alumno,String> nameCol;
    @FXML
    private TableColumn<Alumno,String> pecCol;
    @FXML
    private Label ntotal;

    final ObservableList<Alumno> data = FXCollections.observableArrayList();
    
    GetData d;
    
    private static final String DESCOMPRIMIDAS = "descomprimidas/";
    private static final String CORREGIDAS = "corregidas/";
    
    File def = null;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {                
        // Set up the alumnos table
        this.nCol.setCellValueFactory(new PropertyValueFactory<>("N"));
        this.periodoCol.setCellValueFactory(new PropertyValueFactory<>("Periodo"));
        this.grupoCol.setCellValueFactory(new PropertyValueFactory<>("Grupo"));
        this.DNICol.setCellValueFactory(new PropertyValueFactory<>("DNI"));
        this.PCCol.setCellValueFactory(new PropertyValueFactory<>("PC"));
        this.nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        this.pecCol.setCellValueFactory(new PropertyValueFactory<>("PEC1"));
        
        this.table.setEditable(false);
        this.table.setItems(this.data);
    }

    public void mnuAbrePEC(ActionEvent event) {
        File source = null;
        File dest = null;
        if (Desktop.isDesktopSupported()) {
        	if (this.table.getSelectionModel().getSelectedIndex()>=0) {
	            Alumno a = this.table.getItems().get(this.table.getSelectionModel().getSelectedIndex());
	            if (a != null) {
	                String dni = a.getDNI();
	                source = new File(def, DESCOMPRIMIDAS.concat(dni));
	                dest = new File(def, CORREGIDAS.concat(dni));
	
	                try {
	                    // move files to corregidas path
	                    FileUtils.moveDirectory(source, dest);
	
	                    // open pdf and database file
	                    File[] list = dest.listFiles();
	                    for (File f : list) {
	                        String ext = f.getName().substring(f.getName().lastIndexOf(".")+1);
	                        if (ext.equalsIgnoreCase("pdf") || ext.equalsIgnoreCase("mdb") || 
	                                ext.equalsIgnoreCase("accdb") || ext.equalsIgnoreCase("odb")) {
	                            Desktop.getDesktop().open(f);
	                        }
	                    }
	                } catch (Exception e) {
	                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
	                    alert.showAndWait();
	                }
	            }
        	}
        }
    }

    public void mnuNotaPEC(ActionEvent event) {
    	if (this.table.getSelectionModel().getSelectedIndex()>=0) {
	        Alumno a = this.table.getItems().get(this.table.getSelectionModel().getSelectedIndex());
	        if (a != null) {
	            String dni = a.getDNI();
	            File folder = new File(def, CORREGIDAS.concat(dni));
	            try {
	                // open pdf and database file
	                File[] list = folder.listFiles();
	                for (File f : list) {
	                    String ext = f.getName().substring(f.getName().lastIndexOf(".")+1);
	                    if (ext.equalsIgnoreCase("pdf")) {
	                        // open pdf, get NOTA PEC1 and update server
	                        PdfReader reader = new PdfReader(f.getAbsolutePath());
	                        AcroFields form = reader.getAcroFields();
	                        Integer n = Integer.parseInt(form.getField("NOTA"));
	                        this.d.updatePEC1(dni, a.getGrupo(), n);
	                        reader.close();
	                        
	                        // update tableview to show new NOTA PEC
	    	                this.data.removeAll(this.data);
	    	                this.LoadTable(a.getPeriodo());
	
	                        // select item again
	                        this.data.forEach((i) -> { 
	                            if (i.getN().equals(a.getN())) {
	                                table.getSelectionModel().select(i);
	                                table.scrollTo(i);
	                            }
	                        });
	                    }
	                }
	            } catch (Exception e) {
	                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
	                alert.showAndWait();
	            }
	        }
    	}
    }
    
    @FXML
    public void mnuClose(ActionEvent event) {
        Stage stage = (Stage) this.table.getScene().getWindow();
        stage.close();
    }
    
    public void SetData(GetData d, File dir, String periodo) {
        this.d = d;
        def = new File(dir,"ST1/PEC1");
        LoadTable(periodo);
    }
    
    public void LoadTable(String periodo) {
        int count = 0;        
        try{
            ResultSet rs = this.d.getCorrigePEC1Rs(periodo);
            while(rs.next()){
                count++;
                this.data.add(LoadAlumno(rs,count));
            }
        } catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
        this.ntotal.setText(count + " registros");
    }
    
    public Alumno LoadAlumno(ResultSet rs, Integer count) {
        Alumno a = new Alumno();
        try {
            a.setPeriodo(rs.getString("Periodo"));
            a.setGrupo(rs.getString("Grupo"));
            a.setDNI(rs.getString("DNI"));
            a.setPC(rs.getString("PC"));
            a.setName(rs.getString("nom"));
            a.setPEC1(rs.getString("PEC1"));
            a.setN(Integer.toString(count));
        } catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
        return a;
    }    
}
