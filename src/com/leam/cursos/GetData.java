package com.leam.cursos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    
    public void importExcelRow(org.apache.poi.ss.usermodel.Row row, String periodo) {
        try {
            Integer fijo = row.getCell(6).getStringCellValue().equalsIgnoreCase("No") ? 0 : 1;
            
            PreparedStatement q;
            q = conn.prepareStatement("INSERT INTO alumnos (Grupo,DNI,nombre,ape1,ape2,PC,fijo,CLASE,"
            		+ "Comentario,provincia,poblacion,email,Periodo,Curso) "
            		+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            q.setString(1,row.getCell(0).getStringCellValue());		// grupo
            q.setString(2,row.getCell(1).getStringCellValue());		// dni
            q.setString(3,row.getCell(2).getStringCellValue());		// nombre
            q.setString(4,row.getCell(3).getStringCellValue());		// ape1
            if (!row.getCell(4).getStringCellValue().isEmpty()) {
            	q.setString(5,row.getCell(4).getStringCellValue());	// ape2
            } else {
            	q.setNull(5, java.sql.Types.VARCHAR);
            }
            Integer pc = (int) row.getCell(5).getNumericCellValue();
            q.setInt(6,pc);											// pc
            q.setInt(7,fijo);										// fijo
            Integer clase = (int) row.getCell(7).getNumericCellValue();
            q.setInt(8,clase);										// clase
            if (!row.getCell(8).getStringCellValue().isEmpty()) {
            	q.setString(9,row.getCell(8).getStringCellValue());	// comentario
            } else {
            	q.setNull(9, java.sql.Types.VARCHAR);
            }
            q.setString(10,row.getCell(9).getStringCellValue());	// provincia
            q.setString(11,row.getCell(10).getStringCellValue());	// poblacion
            q.setString(12,row.getCell(12).getStringCellValue());	// email
            q.setString(13,periodo);								// periodo
            q.setString(14,row.getCell(0).getStringCellValue().substring(0,3));		// curso
            //System.out.println(q);
            q.executeUpdate();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
    }

    public void entregaPEC1(String dni, Boolean mdb, Boolean pdf, Boolean honor) {
        try {
            PreparedStatement q;
            q = conn.prepareStatement("INSERT INTO entregahonorpec1 (DNI,entregada,mdb,pdf,honor) VALUES(?,?,?,?,?)");
            q.setString(1, dni);
            q.setBoolean(2, true);
            q.setBoolean(3, mdb);
            q.setBoolean(4, pdf);
            q.setBoolean(5, honor);
            q.executeUpdate();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
    }

    public void entregaPEC(String dni, String curso, String periodo, Boolean honor) {
        try {
            PreparedStatement q;
            q = conn.prepareStatement("INSERT INTO entregahonor (DNI, Curso, Periodo, entregada, honor) VALUES(?, ?, ?, ?, ?)");
            q.setString(1, dni);
            q.setString(2, curso);
            q.setString(3, periodo);
            q.setBoolean(4, true);
            q.setBoolean(5, honor);
            q.executeUpdate();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
    }

}
