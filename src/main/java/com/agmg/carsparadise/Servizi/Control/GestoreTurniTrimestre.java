package com.agmg.carsparadise.Servizi.Control;

import com.agmg.carsparadise.Servizi.Object.Impiegato;
import com.agmg.carsparadise.Servizi.Object.Turno;
import com.agmg.carsparadise.Util.ProcessoDBMS;
import com.agmg.carsparadise.Util.ServerMail;

import java.time.LocalDate;
import java.util.ArrayList;

public class GestoreTurniTrimestre {
    public void start(){
        ArrayList<Impiegato> listaImpiegato = ProcessoDBMS.recuperaImpiegati();
        ArrayList<String> listaRuoli = new ArrayList<>();

        listaRuoli.add("Venditore");
        listaRuoli.add("Noleggiatore");
        listaRuoli.add("Meccanico");
        listaRuoli.add("Addetto al lavaggio");
        // inizio del mese e fine del trimestre
        LocalDate dataOdierna = LocalDate.now().minusDays(26);
        LocalDate dataInizioTrimestre = dataOdierna;
        LocalDate dataFineTrimestre = dataOdierna.plusMonths(3).minusDays(1);

        int garanziaMattina, garanziaPomeriggio, flagAlterna;
        for (String ruolo: listaRuoli) {
            garanziaMattina = 0;
            garanziaPomeriggio = 0;
            flagAlterna = 0;
            for (Impiegato impiegato : listaImpiegato){
                dataOdierna = dataInizioTrimestre;
                if(getServizio(impiegato.getRuolo()).equals(ruolo)){
                    while(!dataOdierna.equals(dataFineTrimestre)) {
                        if(dataOdierna.getDayOfWeek().getValue() != 7){
                            if(garanziaMattina >= 2 && garanziaPomeriggio >= 2){
                                if(flagAlterna == 0){
                                    ProcessoDBMS.inserisciTurnoMattina(dataOdierna.toString(), getServizio(impiegato.getRuolo()), impiegato.getMatricola());
                                    flagAlterna = 1;
                                }else{
                                    ProcessoDBMS.inserisciTurnoPomeriggio(dataOdierna.toString(), getServizio(impiegato.getRuolo()), impiegato.getMatricola());
                                    flagAlterna = 0;
                                }
                            }else if(garanziaMattina >= 2 && garanziaPomeriggio < 2){
                                ProcessoDBMS.inserisciTurnoPomeriggio(dataOdierna.toString(), getServizio(impiegato.getRuolo()), impiegato.getMatricola());
                            }else if(garanziaMattina < 2){
                                ProcessoDBMS.inserisciTurnoMattina(dataOdierna.toString(), getServizio(impiegato.getRuolo()), impiegato.getMatricola());
                            }
                        }
                        dataOdierna = dataOdierna.plusDays(1);
                    }
                    if(garanziaMattina < 2){
                        ++garanziaMattina;
                    }else{
                        ++garanziaPomeriggio;
                    }
                }
            }
        }

        // Invio Email
        for (Impiegato impiegato: listaImpiegato) {
            ArrayList<Turno> listaTurni = ProcessoDBMS.recuperaListaTurni(impiegato.getMatricola(), dataInizioTrimestre.toString());
            String email = ProcessoDBMS.recuperaEmail(impiegato.getMatricola());
            String messaggio = "";
            for (Turno turno: listaTurni) {
                messaggio += "Data Inizio: " + turno.getDataInizio() + " Data Fine: " + turno.getDataFine() + " Servizio: " + turno.getServizio() + "\n\n";
            }
            ServerMail.sendEmail(email, messaggio, "Turni trimestre");
        }
    }

    public String getServizio(int ruolo){
        String servizio = "";
        switch(ruolo){
            case 1:
                servizio = "Venditore";
                break;
            case 2:
                servizio = "Noleggiatore";
                break;
            case 3:
                servizio = "Meccanico";
                break;
            case 4:
                servizio = "Addetto al lavaggio";
                break;
        }
        return servizio;
    }
}

