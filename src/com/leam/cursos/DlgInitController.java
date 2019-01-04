package com.leam.cursos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.leam.cursos.Sintaxis.TipoSintaxis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DlgInitController implements Initializable {

    @FXML
    private RadioButton st1;
    @FXML
    private RadioButton st2;
    @FXML
    private TextField periodo;
    @FXML
    private TextField folder;

    GetData d;
    
    private final File home = new File(System.getProperty("user.home"));
    File dir = null;
    
    private static final String ST1_PEC1_zips = "/ST1/PEC1/comprimidas";
    private static final String ST1_PEC1_unzip = "/ST1/PEC1/descomprimidas";
    private static final String ST1_orig = "/ST1/PEC2/originales";
    private static final String ST2_orig = "/ST2/originales"; 
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void mnuEntregaPEC(ActionEvent event) {
    	// get PECs folder
    	if (this.periodo.getText().isEmpty() || this.folder.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "El período y la carpeta de trabajo son necesarios");
            alert.showAndWait();
    	} else {
	    	String curs = null;
	    	File orig = null;
	    	if (this.st1.selectedProperty().getValue()) {
	    		curs = "ST1";
	    		orig = new File(this.dir, ST1_orig);
	    	}
	    	if (this.st2.selectedProperty().getValue()) {
	    		curs = "ST2";
	    		orig = new File(this.dir, ST2_orig);
	    	}
	
	        // get the PDF files of orig
	        FilenameFilter pdfFilter = (File dir1, String name) -> name.toLowerCase().endsWith(".pdf");
	        File[] pecs = orig.listFiles(pdfFilter);
	
	        // loop through the PEC files
	        StringBuilder problems = new StringBuilder("");
	        boolean lproblems = false;
	        for (File pec : pecs) {
	            if (pec.isFile()) {
	                String n = pec.getName();
	                String dni = n.substring(n.lastIndexOf("_")+1,n.lastIndexOf(".pdf"));      //student's dni
	
	                try {
	                    boolean honor = false;
	                    PdfReader reader = new PdfReader(pec.getAbsolutePath());
	                    AcroFields form = reader.getAcroFields();
	                    String prod = reader.getInfo().get("Producer");
	                    if (prod.toUpperCase().contains("LibreOffice".toUpperCase()) |
	                            form.getFields().size()>0) {
	                        honor = (form.getField("HONOR").equalsIgnoreCase("yes"));   //get honor field
	                        this.d.entregaPEC(dni, curs, this.periodo.getText(), honor);
	                    } else {
	                        lproblems = true;
	                        problems.append(dni + " ; " + prod + "\n");      			//the pdf is not readable
	                    }
	                } catch (Exception e) {
	                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
	                    alert.showAndWait();
	                }
	            }
	        }
	        
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        if (lproblems) {
	        	alert.setTitle("Proceso finalizado");
	        	alert.setHeaderText(null);
	        	alert.setContentText("Se encontraron PECs con problemas");
	
	        	Label label = new Label("PECs afectadas:");
	
	        	TextArea textArea = new TextArea(problems.toString());
	        	textArea.setEditable(false);
	        	textArea.setWrapText(true);
	        	textArea.setMaxWidth(Double.MAX_VALUE);
	        	textArea.setMaxHeight(Double.MAX_VALUE);
	        	GridPane.setVgrow(textArea, Priority.ALWAYS);
	        	GridPane.setHgrow(textArea, Priority.ALWAYS);
	
	        	GridPane expContent = new GridPane();
	        	expContent.setMaxWidth(Double.MAX_VALUE);
	        	expContent.add(label, 0, 0);
	        	expContent.add(textArea, 0, 1);
	
	        	// Set expandable Exception into the dialog pane.
	        	alert.getDialogPane().setExpandableContent(expContent);
	        } else {
	        	alert.setTitle("Proceso finalizado");
	        	alert.setHeaderText(null);
	        	alert.setContentText("Proceso finalizado");
	        }
	        alert.showAndWait();
    	}
    }
    
    @FXML
    public void mnuExtractPEC1(ActionEvent event) {

    	if (this.dir != null) {
            File zips = new File(this.dir, ST1_PEC1_zips);
            File unzip = new File(this.dir, ST1_PEC1_unzip);
            // get all zip files
            FilenameFilter zipFilter;
            zipFilter = (File dir1, String name) -> {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".zip");
            };
            File[] listOfFiles = zips.listFiles(zipFilter);

            Boolean lProblems = false;
            String cProblems = "";
            for (File file : listOfFiles) {
                if (file.isFile()) {
                	byte[] buffer = new byte[1024];
                    
                	// get DNI from file name
                    String n = file.getName();
                    String dni = n.substring(n.lastIndexOf("_")+1,n.lastIndexOf("."));

                    // create output directory if it doesn't exists
                    File dir = new File(unzip.getAbsolutePath(), dni);
                    if (!dir.exists()) dir.mkdir();

                    try {
                        // get the zip file content
                        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
                        //get the zipped file list entry
                        ZipEntry ze = zis.getNextEntry();
                        while (ze!=null) {
                            String fileName = ze.getName();
                            File newFile = new File(dir + File.separator + fileName);

                            // create all non exists folders else you will hit FileNotFoundException for compressed folder
                            new File(newFile.getParent()).mkdirs();

                            FileOutputStream fos = new FileOutputStream(newFile);             
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            
                            ze = zis.getNextEntry();
                        }
                        zis.closeEntry();
                        zis.close();
                    } catch (Exception e) {
                        // If there are no fields on the form or the producer is not LibreOffice, the PDF file may be corrupted
                        lProblems = true;
                        cProblems = cProblems + dni + "\n";
                    }
                }
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (lProblems) {
            	alert.setTitle("Proceso finalizado");
            	alert.setHeaderText(null);
            	alert.setContentText("Hubo problemas al extraer las PECs");

            	Label label = new Label("PECs afectadas:");

            	TextArea textArea = new TextArea(cProblems);
            	textArea.setEditable(false);
            	textArea.setWrapText(true);
            	textArea.setMaxWidth(Double.MAX_VALUE);
            	textArea.setMaxHeight(Double.MAX_VALUE);
            	GridPane.setVgrow(textArea, Priority.ALWAYS);
            	GridPane.setHgrow(textArea, Priority.ALWAYS);

            	GridPane expContent = new GridPane();
            	expContent.setMaxWidth(Double.MAX_VALUE);
            	expContent.add(label, 0, 0);
            	expContent.add(textArea, 0, 1);

            	// Set expandable Exception into the dialog pane.
            	alert.getDialogPane().setExpandableContent(expContent);
            } else {
            	alert.setTitle("Proceso finalizado");
            	alert.setHeaderText(null);
            	alert.setContentText("Proceso finalizado");
            }
            
            alert.showAndWait();
    	}
    }
    
    @FXML
    public void mnuEntregaPEC1(ActionEvent event) {
    	// get PECs folder
    	if (this.folder.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "La carpeta de trabajo es necesaria");
            alert.showAndWait();
    	} else {
	        File pecs = new File(this.dir, ST1_PEC1_unzip);		// get PECs folder
	        File[] folders = pecs.listFiles();					// get PEC1 folders
	        for (File folder : folders) {
	            if (folder.isDirectory()) {
	                String dni = folder.getName();            
	                // get list of files for the dni and confirm PEC1 elements
	                boolean foundMdb = false;
	                boolean foundPdf = false;
	                boolean honor = false;
	                File[] listOfFiles = folder.listFiles();
	                for (File file : listOfFiles) {
	                    if (file.isFile()) {
	                        String ext = file.getName().toLowerCase().substring(file.getName().lastIndexOf(".")+1);     //file extension
	                            
	                        // there's a database
	                        if (ext.equals("mdb") || ext.equals("accdb") || ext.equals("odb")) foundMdb = true;
	                            
	                        // there's a pdf form file
	                        if (ext.equals("pdf")) {
	                            foundPdf = true;                                
	                            // open pdf file
	                            try {
	                                PdfReader reader = new PdfReader(file.getAbsolutePath());
	                                AcroFields form = reader.getAcroFields();
	                                // get honor field
	                                if (form.getFields().size()>0) honor = (form.getField("HONOR").equalsIgnoreCase("yes"));
	                            } catch (Exception e) {
	                                Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
	                                alert.showAndWait();
	                            }
	                        }
	                    }
	                }
	                    
	                this.d.entregaPEC1(dni, foundMdb, foundPdf, honor);
	            }
	        }
    	}
    	
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Proceso finalizado");
        alert.showAndWait();
    }

    @FXML
    public void mnuImportar(ActionEvent event) {
    	String periodo = this.periodo.getText();
    	if (!periodo.isEmpty()) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Abrir archivo de datos");
            chooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                try {
                    FileInputStream input = new FileInputStream(file.getAbsolutePath());
					HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(input));
                    HSSFSheet sheet = wb.getSheetAt(0);
					org.apache.poi.ss.usermodel.Row row;
                    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                        row = sheet.getRow(i);
                        this.d.importExcelRow(row, periodo);
                    }
                    input.close();
                    wb.close();
                    
                    Alert alert = new Alert(AlertType.INFORMATION, "Importación finalizada");
                    alert.setTitle("Importación finalizada");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
                    alert.showAndWait();
                }
            }    		
    	} else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Falta el período");
            alert.showAndWait();
    	}
    }

    @FXML
    public void mnuImportPEC(ActionEvent event) {
    	String periodo = this.periodo.getText();
    	String curso = "";
    	if (this.st1.selectedProperty().getValue()) curso = "ST1";
    	if (this.st2.selectedProperty().getValue()) curso = "ST2";
    	if (!periodo.isEmpty()) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Importar estructura de PEC");
            chooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                try {
                    BufferedReader b = new BufferedReader(new FileReader(file));
                    String readLine = "";
                    while ((readLine = b.readLine()) != null) {
                    	String[] t = readLine.split(",");
                    	this.d.insertPreguntaSol(periodo, curso,
                    			t[1].replaceAll("'",""), Integer.parseInt(t[2]), 
                    			t[3].replaceAll("'",""), Float.parseFloat(t[4]), 
                    			t[5]);
                    }
                    b.close();
                    
                    Alert alert = new Alert(AlertType.INFORMATION, "Importación finalizada");
                    alert.setTitle("Importación finalizada");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
                    alert.showAndWait();
                }
            }    		
    	} else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Falta el período");
            alert.showAndWait();
    	}
    }    
    
    @FXML
    public void mnuSintaxis(ActionEvent event) {
    	// get PECs folder
    	if (this.folder.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "La carpeta de trabajo es necesaria");
            alert.showAndWait();
    	} else {
	    	try {
	            FXMLLoader fxml = new FXMLLoader(getClass().getResource("FXMLsintaxis.fxml"));
	            Parent r = (Parent) fxml.load();
	            FXMLsintaxisController dlg = fxml.<FXMLsintaxisController>getController();
	            dlg.SetData(dir,(this.st1.selectedProperty().getValue() ? TipoSintaxis.ST1 : TipoSintaxis.ST2));
	
	            Stage stage = new Stage();
	            stage.initModality(Modality.APPLICATION_MODAL);
	            stage.setScene(new Scene(r));
	            stage.setTitle("Definir Exportación de Sintaxis");
	            stage.showAndWait();
	        } catch(Exception e) {
	            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
	            alert.showAndWait();
	        }
    	}
    }
    
    @FXML
    public void mnuCorregir(ActionEvent event) {
    	// get PECs folder
    	if (this.periodo.getText().isEmpty() || this.folder.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "El período y la carpeta de trabajo son necesarios");
            alert.showAndWait();
    	} else {
	        try {
	            FXMLLoader fxml = new FXMLLoader(getClass().getResource("FXMLcorregir.fxml"));
	            Parent r = (Parent) fxml.load();
	            FXMLcorregirController dlg = fxml.<FXMLcorregirController>getController();
	            dlg.SetData(this.d, this.dir, this.periodo.getText(), 
	            		(this.st1.selectedProperty().getValue() ? TipoSintaxis.ST1 : TipoSintaxis.ST2));
	
	            Stage stage = new Stage();
	            stage.setScene(new Scene(r));
	            stage.setTitle("Corregir");
	            stage.show();
	        } catch(Exception e) {
	            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
	            alert.showAndWait();
	        }
    	}
    }

    @FXML
    public void mnuCorregirPEC1(ActionEvent event) {
    	// get PECs folder
    	if (this.periodo.getText().isEmpty() || this.folder.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "El período y la carpeta de trabajo son necesarios");
            alert.showAndWait();
    	} else {
	        try {
	            FXMLLoader fxml = new FXMLLoader(getClass().getResource("FXMLcorregirPEC1.fxml"));
	            Parent r = (Parent) fxml.load();
	            FXMLcorregirPEC1Controller dlg = fxml.<FXMLcorregirPEC1Controller>getController();
	            dlg.SetData(this.d, this.dir, this.periodo.getText());
	
	            Stage stage = new Stage();
	            stage.setScene(new Scene(r));
	            stage.setTitle("Corregir PEC1");
	            stage.show();
	        } catch(Exception e) {
	            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
	            alert.showAndWait();
	        }
    	}
    }
    
    public void SetData(GetData d) {
        this.d = d;
    }

    @FXML
    void pbFolder(ActionEvent event) {
        // get working folder
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Escoger carpeta de trabajo");
        chooser.setInitialDirectory(this.home);
        this.dir = chooser.showDialog(null);
        
        if (this.dir != null) {
        	this.folder.setText(this.dir.getAbsolutePath());
        }
    }
    
    @FXML
    void pbCancelar(ActionEvent event) {
        Stage stage = (Stage) this.periodo.getScene().getWindow();
        stage.close();
    }
}
