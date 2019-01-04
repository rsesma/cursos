/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leam.cursos;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.leam.cursos.Sintaxis.TipoSintaxis;

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
public class FXMLcorregirController implements Initializable {

    @FXML
    private TableView<Alumno> table;
    @FXML
    private TableColumn<Alumno,String> nCol;
    @FXML
    private TableColumn<Alumno,String> periodoCol;
    @FXML
    private TableColumn<Alumno,String> cursoCol;
    @FXML
    private TableColumn<Alumno,String> grupoCol;
    @FXML
    private TableColumn<Alumno,String> DNICol;
    @FXML
    private TableColumn<Alumno,String> PCCol;
    @FXML
    private TableColumn<Alumno,String> nameCol;
    @FXML
    private TableColumn<Alumno,String> claseCol;
    @FXML
    private TableColumn<Alumno,String> pecCol;    
    @FXML
    private TableColumn<Alumno,String> copiaCol;
    @FXML
    private TableColumn<Alumno,String> idCopiaCol;
    @FXML
    private Label ntotal;

    final ObservableList<Alumno> data = FXCollections.observableArrayList();
    
    GetData d;
    
    private static final String ST1 = "ST1";
    private static final String ST1_pre = "/PEC2_ST1_";
    private static final String ST2 = "ST2";
    private static final String ST2_pre = "/PEC1_ST2_";
    private static final String Originales = "originales";
    private static final String Corregidas = "corregidas";
    private static final String Sintaxis = "sintaxis";

    File def = null;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
                
        // Set up the alumnos table
        this.nCol.setCellValueFactory(new PropertyValueFactory<>("N"));
        this.periodoCol.setCellValueFactory(new PropertyValueFactory<>("Periodo"));
        this.cursoCol.setCellValueFactory(new PropertyValueFactory<>("Curso"));
        this.grupoCol.setCellValueFactory(new PropertyValueFactory<>("Grupo"));
        this.DNICol.setCellValueFactory(new PropertyValueFactory<>("DNI"));
        this.PCCol.setCellValueFactory(new PropertyValueFactory<>("PC"));
        this.nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        this.claseCol.setCellValueFactory(new PropertyValueFactory<>("Clase"));
        this.pecCol.setCellValueFactory(new PropertyValueFactory<>("PEC"));
        this.idCopiaCol.setCellValueFactory(new PropertyValueFactory<>("IDCopia"));
        this.copiaCol.setCellValueFactory(new PropertyValueFactory<>("Copia"));

        this.table.setEditable(false);
        this.table.setItems(this.data);
    }

    @FXML
    public void mnuAbrePEC(ActionEvent event) {
        if (Desktop.isDesktopSupported()) {
        	if (this.table.getSelectionModel().getSelectedIndex()>=0) {
	            Alumno a = this.table.getItems().get(this.table.getSelectionModel().getSelectedIndex());
	            if (a != null) {
	                String dni = a.getDNI();
	                String curso = a.getCurso();
	                String pre = "";
	                if (curso.equalsIgnoreCase("ST1")) pre = ST1_pre;
	                if (curso.equalsIgnoreCase("ST2")) pre = ST2_pre;
	                File pdfSource = new File(def, Originales + pre + dni + ".pdf");
	                File pdfDest = new File(def, Corregidas + pre + dni + ".pdf");
	                File doFile = new File(def, Sintaxis + "/" + dni + ".do");
	                try {
	                	Files.move(pdfSource.toPath(), pdfDest.toPath());
	                    Desktop.getDesktop().open(pdfDest);
	                    Desktop.getDesktop().open(doFile);
	                } catch (Exception e) {
	                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
	                    alert.showAndWait();
	                }
	            }
        	}
        }
    }

    @FXML
    public void mnuNotaPEC(ActionEvent event) {
    	if (this.table.getSelectionModel().getSelectedIndex()>=0) {
	        Alumno a = this.table.getItems().get(this.table.getSelectionModel().getSelectedIndex());
	        if (a != null) {
	            String dni = a.getDNI();
	            String curso = a.getCurso();
	            String pre = "";
	            if (curso.equalsIgnoreCase("ST1")) pre = ST1_pre;
	            if (curso.equalsIgnoreCase("ST2")) pre = ST2_pre;
	            File pdf = new File(def, Corregidas + pre + dni + ".pdf");
	
	            try {
	                PdfReader reader = new PdfReader(pdf.getAbsolutePath());
	                AcroFields form = reader.getAcroFields();
	
	                ResultSet rs = this.d.getPreguntasRs(a.getPeriodo(),curso);
	                while(rs.next()){
	                    String p = rs.getString("pregunta");
	                    String r = form.getField("P"+p).replace(".", ",");
	                    
	                    this.d.insertRespuesta(a.getPeriodo(), curso, dni, p, r);
	                }
	                reader.close();
	                rs.close();
	           
	                // update tableview to show new NOTA PEC
	                this.data.removeAll(this.data);
	                this.LoadTable(a.getPeriodo(), curso);
	                
	                // select item again
	                this.data.forEach((i) -> { 
	                    if (i.getN().equals(a.getN())) {
	                        table.getSelectionModel().select(i);
	                        table.scrollTo(i);
	                    }
	                });
	            } catch (Exception e) {
	                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
	                alert.showAndWait();
	            }
	        }
    	}
    }
    
    @FXML
    public void mnuAbrirCorr(ActionEvent event) {
        if (Desktop.isDesktopSupported()) {
        	if (this.table.getSelectionModel().getSelectedIndex()>=0) {
	            Alumno a = this.table.getItems().get(this.table.getSelectionModel().getSelectedIndex());
	            if (a != null) {
	                String dni = a.getDNI();
	                String curso = a.getCurso();
	                String pre = "";
	                if (curso.equalsIgnoreCase("ST1")) pre = ST1_pre;
	                if (curso.equalsIgnoreCase("ST2")) pre = ST2_pre;
	                File pdf = new File(def, Corregidas + pre + dni + ".pdf");

	                try {
	                    Desktop.getDesktop().open(pdf);
	                } catch (Exception e) {
	                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
	                    alert.showAndWait();
	                }
	            }
        	}
        }
    }
    
    
    @FXML
    public void mnuClose(ActionEvent event) {
        Stage stage = (Stage) this.ntotal.getScene().getWindow();
        stage.close();
    }
    
    public void SetData(GetData d, File dir, String periodo, TipoSintaxis type) {
        this.d = d;
        String curso = "";
    	if (type == TipoSintaxis.ST1) {
    		def = new File(dir,ST1+"/PEC2");
    		curso = ST1;
    	}
    	if (type == TipoSintaxis.ST2) {
    		def = new File(dir,ST2);
    		curso = ST2;
    	}
        LoadTable(periodo,curso);
    }
    
    
    public void LoadTable(String periodo, String curso) {
        int count = 0;
        try{
            ResultSet rs = this.d.getCorrigeRs(periodo,curso);
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
            a.setCurso(rs.getString("Curso"));
            a.setGrupo(rs.getString("Grupo"));
            a.setDNI(rs.getString("DNI"));
            a.setPC(rs.getString("PC"));
            a.setName(rs.getString("nom"));
            a.setClase(Notas.getByCode(rs.getString("CLASE")).toString());
            a.setPEC(rs.getString("Nota_PEC"));
            a.setCopia(rs.getBoolean("Copia"));
            a.setIDCopia(rs.getString("IDcopia"));
            a.setN(Integer.toString(count));
        } catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
        return a;
    }
}
