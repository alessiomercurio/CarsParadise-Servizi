package com.agmg.carsparadise.Servizi.Object;

public class Impiegato {

    private int matricola;
    private int ruolo;

    public Impiegato(int matricola, int ruolo) {
        this.matricola = matricola;
        this.ruolo = ruolo;
    }

    public int getMatricola() {
        return matricola;
    }

    public void setMatricola(int matricola) {
        this.matricola = matricola;
    }

    public int getRuolo() {
        return ruolo;
    }

    public void setRuolo(int ruolo) {
        this.ruolo = ruolo;
    }

}
