package com.agmg.carsparadise.Servizi.Control;

import com.agmg.carsparadise.Servizi.Object.Impiegato;
import com.agmg.carsparadise.Servizi.Object.Turno;
import com.agmg.carsparadise.Util.ProcessoDBMS;
import com.agmg.carsparadise.Util.ServerMail;
import java.util.concurrent.ThreadLocalRandom;

import java.time.LocalDate;
import java.util.ArrayList;

public class GestoreServizi {

    public void start(){
        String time = "14:00:00";
        LocalDate dataOdierna = LocalDate.now().plusDays(1); //23 gennaio, otteni
        ArrayList<Turno> listaTurni = ProcessoDBMS.recuperaTurniCorrenti(dataOdierna.toString(),time);
        int[] numeroTurnIPerServizio = contaTurniPerServizio(listaTurni);
        ArrayList<String> listaServizi = new ArrayList<>();
        for(int i=0; i<4; ++i){
            System.out.println(numeroTurnIPerServizio[i]);
        }
        listaServizi.add("Venditore");
        listaServizi.add("Noleggiatore");
        listaServizi.add("Meccanico");
        listaServizi.add("Addetto al lavaggio");
        int servizioCorrente = 0, servizioDaAttingere; // i = servizio che controlliamo, j = servizio da cui attingere
        boolean stopLoop = false;
        for (String servizio : listaServizi) {
            servizioDaAttingere = 3;
            while(numeroTurnIPerServizio[servizioCorrente] < 2 && servizioCorrente != servizioDaAttingere){
                if(numeroTurnIPerServizio[servizioDaAttingere] > 0){
                    ProcessoDBMS.spostaTurno(servizio, getServizio(servizioDaAttingere), dataOdierna.toString(), time);
                    --numeroTurnIPerServizio[servizioDaAttingere];
                    ++numeroTurnIPerServizio[servizioCorrente];
                }else if (numeroTurnIPerServizio[servizioDaAttingere] == 0) {
                    --servizioDaAttingere;
                }
            }
            ++servizioCorrente;
        }
        ArrayList<String> listaEmail = ProcessoDBMS.recuperaEmailAmministratori();
        for (int i = 0; i<4; ++i){
            if((numeroTurnIPerServizio[i] == 0 || numeroTurnIPerServizio[i] == 1) && i == 0){
                // algoritmo per chiamare
                /*
                * Prendere tutti gli impiegati che non hanno turni in questa fascia oraria e periodi di astensioni per questa giornata
                * Prendiamo un impiegato a caso
                * Aggiungiamo il turno straordinario per vendita
                *
                * */
                while(numeroTurnIPerServizio[0] < 2){
                    ArrayList<Impiegato> listaImpiegatiDaChiamare = ProcessoDBMS.recuperaImpiegatiSenzaTurni(dataOdierna.toString(), time);
                    //chiamare impiegato casuale
                    int randomNum = ThreadLocalRandom.current().nextInt(0, listaImpiegatiDaChiamare.size());
                    ProcessoDBMS.inserisciTurnoStraordinaio(dataOdierna.toString(), time, listaImpiegatiDaChiamare.get(randomNum).getMatricola(), "Venditore");
                    ++numeroTurnIPerServizio[0];
                    String emailImpiegato = ProcessoDBMS.recuperaEmail(listaImpiegatiDaChiamare.get(randomNum).getMatricola());
                    ServerMail.sendEmail(emailImpiegato, "E' stato aggiunto un turno straordinario per il " + dataOdierna.toString() + "alle ore " + time + " per il servizio di Vendita", "Turno Straordinario");
                }
            }else if(numeroTurnIPerServizio[i] == 1){
                ProcessoDBMS.spostaTurno("Venditore", getServizio(i), dataOdierna.toString(), time);
            }
            if(numeroTurnIPerServizio[i] == 0){
                for (String email: listaEmail) {
                    ServerMail.sendEmail(email, "Il servizio " + getServizio(i) + " è stato chiuso", "Chiusura giornaliera servizi");
                }
            }
        }
    }

    private String getServizio(int indice){
        String servizio = "";
        switch (indice){
            case 0:
                servizio = "Venditore";
                break;
            case 1:
                servizio = "Noleggiatore";
                break;
            case 2:
                servizio = "Meccanico";
                break;
            case 3:
                servizio = "Addetto al lavaggio";
                break;

        }
        return servizio;
    }

    public int[] contaTurniPerServizio(ArrayList<Turno> lista){ // la i va --->, come fai a fare <---
        int contatoreVenditore = 0, contatoreNoleggiatore = 0, contatoreMeccanico = 0, contatoreAddetto = 0;
        for(Turno turno : lista){
            switch (turno.getServizio()){
                case "Venditore":
                    ++contatoreVenditore;
                    break;
                case "Noleggiatore":
                    ++contatoreNoleggiatore;
                    break;
                case "Meccanico":
                    ++contatoreMeccanico;
                    break;
                case "Addetto al lavaggio":
                    ++contatoreAddetto;
                    break;
            }
        }
        return new int[]{contatoreVenditore, contatoreNoleggiatore, contatoreMeccanico, contatoreAddetto};
    }



}
