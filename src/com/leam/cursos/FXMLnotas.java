/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leam.cursos;

import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.leam.cursos.Sintaxis.TipoSintaxis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author r
 */
public class FXMLnotas implements Initializable {

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
    private TableColumn<Alumno,String> pec1Col;    
    @FXML
    private TableColumn<Alumno,String> pecCol;    
    @FXML
    private TableColumn<Alumno,String> notaCol;    
    @FXML
    private TableColumn<Alumno,String> copiaCol;
    @FXML
    private TableColumn<Alumno,String> idCopiaCol;
    @FXML
    private TextField search;
    @FXML
    private Button btSearch;
    @FXML
    private Button btClean;
    @FXML
    private Label ntotal;

    final ObservableList<Alumno> data = FXCollections.observableArrayList();
    
    GetData d;
    String periodo = null;
    String curso = null;
        
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    	ImageView imgSearch = new ImageView(new Image(getClass().getResourceAsStream("search.png")));
    	imgSearch.setFitWidth(15);
    	imgSearch.setFitHeight(15);
        this.btSearch.setGraphic(imgSearch);
            
    	ImageView imgClean = new ImageView(new Image(getClass().getResourceAsStream("no_filter.png")));
    	imgClean.setFitWidth(15);
    	imgClean.setFitHeight(15);
        this.btClean.setGraphic(imgClean);

        // Set up the alumnos table
        this.nCol.setCellValueFactory(new PropertyValueFactory<>("N"));
        this.periodoCol.setCellValueFactory(new PropertyValueFactory<>("Periodo"));
        this.cursoCol.setCellValueFactory(new PropertyValueFactory<>("Curso"));
        this.grupoCol.setCellValueFactory(new PropertyValueFactory<>("Grupo"));
        this.DNICol.setCellValueFactory(new PropertyValueFactory<>("DNI"));
        this.PCCol.setCellValueFactory(new PropertyValueFactory<>("PC"));
        this.nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        this.claseCol.setCellValueFactory(new PropertyValueFactory<>("Clase"));
        this.pec1Col.setCellValueFactory(new PropertyValueFactory<>("PEC1"));
        this.pecCol.setCellValueFactory(new PropertyValueFactory<>("PEC"));
        this.notaCol.setCellValueFactory(new PropertyValueFactory<>("NOTA"));
        this.copiaCol.setCellValueFactory(new PropertyValueFactory<>("Copia"));
        this.idCopiaCol.setCellValueFactory(new PropertyValueFactory<>("IDCopia"));

        this.table.setEditable(false);
        this.table.setItems(this.data);
    }
    
    @FXML
    public void pbSearch(ActionEvent event) {
        String filter = "";
        if (!this.search.getText().trim().isEmpty()) {
            filter = "AND (DNI LIKE '%".concat(this.search.getText()).concat("%'");
            filter = filter.concat(" OR nom LIKE '%").concat(this.search.getText()).concat("%')");
            this.data.removeAll(this.data);
            LoadTable(filter);
        }
    }

    @FXML
    public void pbClean(ActionEvent event) {
        this.data.removeAll(this.data);
        this.search.setText("");
        LoadTable("");
    }
    
    public void SetData(GetData d, File dir, String periodo, TipoSintaxis type) {
        this.d = d;
        this.periodo = periodo;
    	if (type == TipoSintaxis.ST1) this.curso = "ST1";
    	if (type == TipoSintaxis.ST2) this.curso = "ST2";
    	if (type == TipoSintaxis.IO1) this.curso = "IO1";
    	if (type == TipoSintaxis.IO2) this.curso = "IO2";
    	if (type == TipoSintaxis.IO3) this.curso = "IO3";
    	LoadTable("");
    }
        
    public void LoadTable(String filter) {
        int count = 0;
        try{
            ResultSet rs = this.d.getNotasRs(this.periodo,this.curso,filter);
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
            String n = rs.getString("CLASE");
            if (!rs.wasNull()) a.setClase(Notas.getByCode(n).toString());
            a.setPEC1(rs.getString("PEC1"));
            a.setPEC(rs.getString("PEC"));
            a.setNOTA(rs.getString("NOTA"));
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
