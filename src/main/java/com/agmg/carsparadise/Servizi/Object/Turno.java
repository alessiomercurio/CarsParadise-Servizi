package com.agmg.carsparadise.Servizi.Object;

public class Turno {

    private String dataInizio;
    private String dataFine;
    private String servizio;
    private int id_Turno;
    private int id_Impiegato;

    private int isStraordinario;

    public Turno(String dataInizio, String dataFine, String servizio, int id_Turno, int id_Impiegato, int isStraordinario) {
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.servizio = servizio;
        this.id_Turno = id_Turno;
        this.id_Impiegato = id_Impiegato;
        this.isStraordinario = isStraordinario;
    }

    public String getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(String dataInizio) {
        this.dataInizio = dataInizio;
    }

    public String getDataFine() {
        return dataFine;
    }

    public void setDataFine(String dataFine) {
        this.dataFine = dataFine;
    }

    public String getServizio() {
        return servizio;
    }

    public void setServizio(String servizio) {
        this.servizio = servizio;
    }

    public int getId_Turno() {
        return id_Turno;
    }

    public void setId_Turno(int id_Turno) {
        this.id_Turno = id_Turno;
    }

    public int getId_Impiegato() {
        return id_Impiegato;
    }

    public void setId_Impiegato(int id_Impiegato) {
        this.id_Impiegato = id_Impiegato;
    }

    public int getIsStraordinario() {
        return isStraordinario;
    }

    public void setIsStraordinario(int isStraordinario) {
        this.isStraordinario = isStraordinario;
    }
}
