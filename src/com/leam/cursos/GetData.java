package com.leam.cursos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javafx.scene.control.Alert;

public class GetData {
    private static final String C_DRIVER = "jdbc:mysql";
    private static Connection conn = null;

    public Boolean getConnection(String user, String pswd, String server) {
        try {
            String url = C_DRIVER + "://" + server + ":3306/alumnos";
            Properties info = new Properties();
            info.setProperty("user", user);
            info.setProperty("password", pswd);
            info.setProperty("useSSL", "false");
            GetData.conn = DriverManager.getConnection(url, info);
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
            return false;
        }
    }
    
    public ResultSet getNotas() throws SQLException {
        return conn.prepareStatement("SELECT * FROM dnotas").executeQuery();
    }


}
