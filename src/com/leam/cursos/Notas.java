/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leam.cursos;

/**
 *
 * @author r
 */
public enum Notas {
        APROBADO("1", "Aprobado"), BIEN("2", "Bien"), NOTABLE("3", "Notable"), EXCELENTE("4", "Excelente");

        private String code;
        private String text;

        private Notas(String code, String text) {
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }

        public static Notas getByCode(String notasCode) {
            for (Notas g : Notas.values()) {
                if (g.code.equals(notasCode)) {
                    return g;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }
