package com.agmg.carsparadise.Util;

import javafx.scene.control.Alert;

public class ErroreDBMS extends Exception {

    public ErroreDBMS(String messaggio) {
        super(messaggio);
    }

    // Metodo da utilizzare per tutti gli errori di connessione
    public static void erroreGenericoDBMS(Exception e) {
        // Deve mostrare un pannello di errore
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Errore DBMS");
        alert.setContentText("E' stato rilevato un errore del DBMS durante l'esecuzione dell'operazione.");
        alert.showAndWait();
    }

}
