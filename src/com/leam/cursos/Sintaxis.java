/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leam.cursos;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import javafx.scene.control.Alert;

/**
 *
 * @author R
 */
public class Sintaxis {
    private final TipoSintaxis tipo;
    private final Integer nTotal;
    private final Integer nPregs;
    private final Pregunta[] pregs;
    private final Integer[] excl;
    
    private String dir;
    private String sheet;
    private String erase;
    
    private static final String ST1_originales = "/PEC2/originales";
    private static final String ST1_sintaxis = "/PEC2/sintaxis/";
    private static final String ST2_originales = "/originales";
    private static final String ST2_sintaxis = "/sintaxis/";
    private static final String newline = System.getProperty("line.separator");
    
    public enum TipoSintaxis {
        ST1, ST2
    }

    public Sintaxis(TipoSintaxis tipo, int nTotal, int nPregs, String excluidas) {
        this.tipo = tipo;
        
        this.nTotal = nTotal;
        this.nPregs = nPregs;
        this.pregs = new Pregunta[this.nPregs];
        
        if (!excluidas.isEmpty()) {
            String[] j = excluidas.split(" ");
            this.excl = new Integer[j.length];
            for (int i=0; i < j.length; i++) {
                excl[i] = Integer.parseInt(j[i]);
            }
        } else {
            this.excl = null;
        }
    }
    
    public void addPregunta(int n, Pregunta p) {
        this.pregs[n] = p;
    }
    
    public void SetDir(String d) {
        this.dir = d;
    }
    
    public void SetSheet(String s) {
        this.sheet = s;
    }
    public void SetErase(String s) {
        this.erase = s;
    }
    
    public void Exportar() {
        // working folders
        File folder = new File(this.dir);
        if (this.tipo == TipoSintaxis.ST1) folder = new File(folder, ST1_originales);
        if (this.tipo == TipoSintaxis.ST2) folder = new File(folder, ST2_originales);
        
        String syntax_dir = this.tipo == TipoSintaxis.ST1 ? ST1_sintaxis : ST2_sintaxis;
                
        String xls = this.tipo == TipoSintaxis.ST1 ? "PEC2_ST1.xlsx" : "PEC1_ST2.xlsx";
        
        String[] del = new String[0];
        if (!this.erase.isEmpty()) del = this.erase.split(" ");
        
        // get pdf files
        FilenameFilter pdfFilter = (File dir1, String name) -> {
            String lowercaseName = name.toLowerCase();
            return (lowercaseName.endsWith(".pdf"));
        };
        File[] listOfFiles = folder.listFiles(pdfFilter);
        
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String n = file.getName();
                String dni = n.substring(n.lastIndexOf("_")+1,n.lastIndexOf(".pdf"));
                
                try{
                    PdfReader reader = new PdfReader(file.getAbsolutePath());
                    AcroFields form = reader.getAcroFields();
                
                    String syntax = "**PEC : " + dni + newline + 
                            "cd " + this.dir + newline +
                            "clear" + newline;
                    if (!this.erase.isEmpty()) {
                        for (String e : del) {
                            syntax = syntax + "capture confirm file " + e + ".dta" + newline +
                                        "if (!_rc) erase " + e + ".dta" + newline;
                        }
                    }
                    syntax = syntax +"import excel " + xls + ", sheet(\"" + this.sheet + "\") firstrow" + 
                            newline + newline;
                
                    for(int i=2; i<=this.nTotal; i++){                        
                        // get syntax from the pdf field
                        String p = String.format("%02d",i);
                        if (!isExcluded(i)) syntax = syntax + "*Pregunta " + p + newline;
                        
                        Pregunta preg = getPregunta(i);
                        if (preg != null) {
                            switch (preg.tipo) {
                                case IMPORT:
                                    syntax = syntax + "import excel " + xls + ", sheet(\"" + preg.data + "\") firstrow clear" + newline + newline +
                                    		form.getField("P"+p+"_B" ) + newline;
                                    break;
                                case TEST:
                                    syntax = syntax + form.getField("P"+p+"_B" ) + newline + newline + 
                                        "merge 1:1 " + preg.id + " using \"" + preg.data + ".dta\", nogenerate" + newline + 
                                        "testvars " + preg.vars + ", p(" + preg.preg + ") id(" + preg.id + ")" + newline;
                                    
                                    switch (this.tipo) {
                                        case ST1:
                                            syntax = syntax + newline;
                                            break;
                                        case ST2:
                                            syntax = syntax + "drop _*" + newline + newline;
                                            break;
                                    }
                                    break;
                            }
                        } else {
                        	syntax = syntax + form.getField("P"+p+"_B" ) + newline + newline;
                        }
                    }
                    reader.close();

                    PrintWriter out = new PrintWriter(this.dir + syntax_dir + dni + ".do");
                    out.println(syntax);
                    out.close();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, e.getMessage());
                    alert.showAndWait();
                }
            }
        }
    }
    
    public boolean isExcluded(int j) {
        if (this.excl != null) {
            for (int i : this.excl){
                if (i == j) return true;
            }
        }
        return false;
    }
    
    public Pregunta getPregunta(int j) {
        for (Pregunta p : this.pregs){
            if (j == p.num) return p;
        }
        return null;
    }
}
