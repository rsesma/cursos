package com.leam.cursos;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLloginController implements Initializable {

    @FXML
    private TextField user;
    @FXML
    private PasswordField pswd;
    @FXML
    private TextField server;
    @FXML
    private TextField db;
    
    public GetData d;
    public Boolean ok = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.d = new GetData();
    }
    
    @FXML
    void pbAceptar(ActionEvent event) {
        if (this.d.getConnection(this.user.getText(),this.pswd.getText(),this.server.getText(),this.db.getText())) {
            this.ok = true;
            closeWindow();
        }
    }

    @FXML
    void pbCancelar(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) this.user.getScene().getWindow();
        stage.close();
    }
}