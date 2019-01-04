
package com.leam.cursos;

public class Alumno {
    private String n;
    private String periodo;
    private String curso;
    private String grupo;
    private String dni;
    private String pc;
    private Boolean fijo;
    private String name;
    private String clase;
    private String pec1;
    private String pec;
    private String nota;
    private String coment;
    private Boolean copia;
    private String idcopia;
        
    public String getN() {
        return this.n;
    }

    public void setN(String c) {
        this.n = c;
    }
    
    public String getPeriodo() {
        return this.periodo;
    }

    public void setPeriodo(String c) {
        this.periodo = c;
    }

    public String getCurso() {
        return this.curso;
    }

    public void setCurso(String c) {
        this.curso = c;
    }

    public String getGrupo() {
        return this.grupo;
    }

    public void setGrupo(String c) {
        this.grupo = c;
    }
    
    public String getDNI() {
        return this.dni;
    }

    public void setDNI(String c) {
        this.dni = c;
    }

    public String getPC() {
        return this.pc;
    }

    public void setPC(String c) {
        this.pc = c;
    }
    
    public Boolean getFijo() {
        return this.fijo;
    }

    public void setFijo(Boolean l) {
        this.fijo = l;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String c) {
        this.name = c;
    }
    
    public String getClase() {
        return this.clase;
    }

    public void setClase(String c) {
        this.clase = c;
    }

    public String getPEC1() {
        return this.pec1;
    }

    public void setPEC1(String c) {
        this.pec1 = c;
    }

    public String getPEC() {
        return this.pec;
    }

    public void setPEC(String c) {
        this.pec = c;
    }
    
    public String getNOTA() {
        return this.nota;
    }

    public void setNOTA(String c) {
        this.nota = c;
    }

    public String getComent() {
        return this.coment;
    }

    public void setComent(String c) {
        this.coment = c;
    }

    public String getCopia() {
        return (this.copia ? "Sí" : "");
    }

    public void setCopia(Boolean l) {
        this.copia = l;
    }

    public void setIDCopia(String c) {
        this.idcopia = c;
    }

    public String getIDCopia() {
        return this.idcopia;
    }
}
