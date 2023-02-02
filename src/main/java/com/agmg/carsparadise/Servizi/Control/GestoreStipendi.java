package com.agmg.carsparadise.Servizi.Control;

import com.agmg.carsparadise.Servizi.Object.Impiegato;
import com.agmg.carsparadise.Servizi.Object.Turno;
import com.agmg.carsparadise.Util.ProcessoDBMS;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class GestoreStipendi {

    public void start(){
        LocalDate dataCorrente= LocalDate.now();

        //if(dataCorrente.getDayOfMonth() != 1){
            LocalDate inizioMesePrecedente = dataCorrente.minusMonths(1).minusDays(25);
            LocalDate fineMesePrecedente = dataCorrente.minusDays(27);

            ArrayList<Impiegato> listaImpiegati = ProcessoDBMS.recuperaImpiegati();
            ArrayList<Integer> listaCompensi = ProcessoDBMS.richiediCompensi();
            double totaleParziale = 0;
            int parziale = 0;

            for (Impiegato impiegato: listaImpiegati) {
                totaleParziale = 0;
                parziale = 0;
                ArrayList<Turno> listaTurniConIngresso = ProcessoDBMS.recuperaTurniConIngresso(impiegato.getMatricola(), inizioMesePrecedente.toString(), fineMesePrecedente.toString());
                for (Turno turno: listaTurniConIngresso) {
                    if (turno.getId_Impiegato() == impiegato.getMatricola()) {
                        int ore = Math.toIntExact(calcoloOre(turno.getDataInizio(), turno.getDataFine()));
                        if (turno.getIsStraordinario() == 0) {
                            switch (turno.getServizio()) {
                                case "Venditore":
                                    parziale = ore * listaCompensi.get(0);
                                    break;
                                case "Noleggiatore":
                                    parziale = ore * listaCompensi.get(2);
                                    break;
                                case "Meccanico":
                                    parziale = ore * listaCompensi.get(4);
                                    break;
                                case "Addetto al lavaggio":
                                    parziale = ore * listaCompensi.get(6);
                                    break;
                            }
                            totaleParziale += parziale;
                        } else {
                            switch (turno.getServizio()) {
                                case "Venditore":
                                    parziale = ore * listaCompensi.get(1);
                                    break;
                                case "Noleggiatore":
                                    parziale = ore * listaCompensi.get(3);
                                    break;
                                case "Meccanico":
                                    parziale = ore * listaCompensi.get(5);
                                    break;
                                case "Addetto al lavaggio":
                                    parziale = ore * listaCompensi.get(7);
                                    break;
                            }
                            totaleParziale += parziale;
                        }
                    }
                }
                ProcessoDBMS.inserisciStipendio(impiegato.getMatricola(), fineMesePrecedente.toString(), totaleParziale);
            }
        }
    //}
    public long calcoloOre(String inizioTurno, String fineTurno){
        LocalTime inizio = LocalTime.parse(inizioTurno);
        LocalTime fine = LocalTime.parse(fineTurno);
        Duration diff = Duration.between(inizio, fine);
        long diffInt = diff.toMinutes() / 60;
        return diffInt;
    }
/*


    public int calcoloStipendiOrdinari(int oreTotali, int compensoOrarioOrdinario){

    }

    public int calcoloStipendiStraordinario(int oreTotali, int compensoOrarioStraordinario){

    }
*/
}
