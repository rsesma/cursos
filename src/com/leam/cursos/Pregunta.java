/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leam.cursos;

/**
 *
 * @author R
 */
public class Pregunta {
    public int num;
    public TipoPregunta tipo;
    public String data;
    public String vars;
    public String preg;
    public String id;
            
    public enum TipoPregunta {
        IMPORT, TEST
    }
    
    public Pregunta(int n, TipoPregunta t, String d, String v, String p, String id) {
        this.num = n;
        this.tipo = t;
        this.data = d;
        this.vars = v;
        this.preg = p;
        this.id = id;
    }
}
