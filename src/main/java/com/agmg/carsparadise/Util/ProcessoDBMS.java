package com.agmg.carsparadise.Util;

import com.agmg.carsparadise.Servizi.Object.Impiegato;
import com.agmg.carsparadise.Servizi.Object.Turno;
import javafx.util.converter.LocalTimeStringConverter;

import java.nio.channels.SelectableChannel;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class ProcessoDBMS {

    //aprire la connessione con il DB
    public static Connection connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/CarsParadise", //url
                    "root", //username
                    "rootalex" //password
            );
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
        return conn;
    }

    //chiudere la connessione con il DB
    public static void closeConnectionToDatabase(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
        }
    }


    public static ArrayList<Impiegato> recuperaImpiegati() {
        ArrayList<Impiegato> lista = new ArrayList<>();
        try {
            Connection conn = connectToDatabase();
            String query = "SELECT Matricola, Ref_Ruolo" +
                    " FROM Impiegato";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                lista.add(new Impiegato(rs.getInt("Matricola"), rs.getInt("Ref_Ruolo")));
            }
            closeConnectionToDatabase(conn);
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
        return lista;
    }

    public static ArrayList<Integer> richiediCompensi() {
        ArrayList<Integer> lista = new ArrayList<>();
        try {
            Connection conn = connectToDatabase();
            String query = "SELECT CompensoOrdinario, CompensoStraordinario" +
                    " FROM Ruolo";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                lista.add(rs.getInt("CompensoOrdinario"));
                lista.add(rs.getInt("CompensoStraordinario"));
            }
            closeConnectionToDatabase(conn);
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
        return lista;
    }

    // struttura array: [ordinarioVendita],[straordinarioVendita] , [ordinarioNoleggio],[straordinarioNoleggio], ...

    public static ArrayList<Turno> recuperaTurniConIngresso(int matricola, String inizioMese, String fineMese) {
        int idTurno = 0;
        String servizio = "";
        String dataInizio = "";
        String dataFine = "";
        int refImpiegato = 0;
        int straordinario = 0;
        ArrayList<Turno> lista = new ArrayList<>();
        try {
            Connection conn = connectToDatabase();
            String query = "SELECT T.id_Turno, T.Servizio, time(T.Data_Inizio) as DataInizio, time(T.Data_Fine) as DataFine, T.Ref_Impiegato, T.isStraordinario" +
                    " FROM Turno T, Ingresso I, Impiegato IM" +
                    " WHERE IM.matricola = T.Ref_Impiegato AND IM.matricola = I.ref_impiegato AND T.id_Turno = I.Ref_Turno AND date(Data_Inizio) BETWEEN " + "date('" + inizioMese + "')" + " AND " + "date('" + fineMese + "')";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                idTurno = rs.getInt("T.id_Turno");
                servizio = rs.getString("T.Servizio");
                dataInizio = rs.getString("DataInizio");dataFine = rs.getString("DataFine");
                refImpiegato = rs.getInt("T.Ref_Impiegato");
                straordinario = rs.getInt("T.isStraordinario");
                lista.add(new Turno(dataInizio, dataFine, servizio, idTurno, refImpiegato, straordinario));
            }
            closeConnectionToDatabase(conn);
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
        return lista;
    }

    public static void inserisciStipendio(int matricola, String dataCorrente, double totale) {
        try {
            Connection conn = connectToDatabase();
            String query = "INSERT INTO Stipendio(Importo, Data, Ref_Impiegato_s)" +
                    " VALUES (" + totale + ", " + "'" + dataCorrente + "', " + matricola + ")";
            Statement stmt = conn.createStatement();
            int result = stmt.executeUpdate(query);
            closeConnectionToDatabase(conn);
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
    }

    public static void inserisciTurnoMattina(String dataInizio, String servizio, int matricola){
        LocalDate data = LocalDate.parse(dataInizio);
        LocalTime mattina = LocalTime.parse("08:00:00");

        String dataInizioTurno = data.toString() + " " + mattina.toString();
        String dataFineTurno = data.toString() + " " + mattina.plusHours(6).toString();

        try {
            Connection conn = connectToDatabase();
            String query = "INSERT INTO Turno(Servizio, Data_Inizio, Data_Fine, Ref_Impiegato, isStraordinario)" +
                    " VALUES(?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, servizio);
            stm.setString(2, dataInizioTurno);
            stm.setString(3, dataFineTurno);
            stm.setInt(4, matricola);
            stm.setInt(5, 0);
            stm.executeUpdate();
            connectToDatabase().close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErroreDBMS.erroreGenericoDBMS(e);
        }
        //String dataInizio, String dataFine, String servizio, int id_Turno, int id_Impiegato, int isStraordinario)
    }

    public static void inserisciTurnoPomeriggio(String dataInizio, String servizio, int matricola){

        LocalDate data = LocalDate.parse(dataInizio);
        LocalTime mattina = LocalTime.parse("14:00:00");

        String dataInizioTurno = data.toString() + " " + mattina.toString();
        String dataFineTurno = data.toString() + " " + mattina.plusHours(6).toString();

        try {
            Connection conn = connectToDatabase();
            String query = "INSERT INTO Turno(Servizio, Data_Inizio, Data_Fine, Ref_Impiegato, isStraordinario)" +
                    " VALUES(?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, servizio);
            stm.setString(2, dataInizioTurno);
            stm.setString(3, dataFineTurno);
            stm.setInt(4, matricola);
            stm.setInt(5, 0);
            stm.executeUpdate();
            connectToDatabase().close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErroreDBMS.erroreGenericoDBMS(e);
        }
    }

    public static ArrayList<Turno> recuperaListaTurni(int matricola, String data){
        ArrayList<Turno> lista = new ArrayList<>();
        int idTurno = 0;
        String servizio = "";
        String dataInizio = "";
        String dataFine = "";
        int refImpiegato = 0;
        int straordinario = 0;
        try {
            Connection conn = connectToDatabase();
            String query = "SELECT T.id_Turno, T.Servizio, T.Data_Inizio as DataInizio, T.Data_Fine as DataFine, T.Ref_Impiegato, T.isStraordinario" +
                    " FROM Turno T" +
                    " WHERE T.Ref_Impiegato = " + matricola + " AND date(T.Data_Inizio) >= " + "'" + data + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                idTurno = rs.getInt("T.id_Turno");
                servizio = rs.getString("T.Servizio");
                dataInizio = rs.getString("DataInizio");dataFine = rs.getString("DataFine");
                refImpiegato = rs.getInt("T.Ref_Impiegato");
                straordinario = rs.getInt("T.isStraordinario");
                lista.add(new Turno(dataInizio, dataFine, servizio, idTurno, refImpiegato, straordinario));
            }
            closeConnectionToDatabase(conn);
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
        return lista;
    }

    public static String recuperaEmail(int matricola){
        String email = "";
        try {
            Connection conn = connectToDatabase();
            String query = "SELECT Email" +
                    " FROM Impiegato" +
                    " WHERE Matricola = " + matricola;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                email = rs.getString("Email");
            }
            closeConnectionToDatabase(conn);
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
        return email;
    }

    public static ArrayList<Turno> recuperaTurniCorrenti(String dataOdierna, String orario){
        ArrayList<Turno> lista = new ArrayList<>();
        int idTurno = 0;
        String servizio = "";
        String dataInizio = "";
        String dataFine = "";
        int refImpiegato = 0;
        int straordinario = 0;
        try {
            Connection conn = connectToDatabase();
            String query = "SELECT T.id_Turno, T.Servizio, date(T.Data_Inizio) as DataInizio, date(T.Data_Fine) as DataFine, T.Ref_Impiegato, T.isStraordinario" +
                    " FROM Turno T" +
                    " WHERE date(T.Data_Inizio) = " + "date('" + dataOdierna + "')" +
                    " AND time(T.Data_Fine) <= time(' " + orario + "') ";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                idTurno = rs.getInt("T.id_Turno");
                servizio = rs.getString("T.Servizio");
                dataInizio = rs.getString("DataInizio");dataFine = rs.getString("DataFine");
                refImpiegato = rs.getInt("T.Ref_Impiegato");
                straordinario = rs.getInt("T.isStraordinario");
                lista.add(new Turno(dataInizio, dataFine, servizio, idTurno, refImpiegato, straordinario));
            }
            closeConnectionToDatabase(conn);
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
        return lista;
    }

    public static void spostaTurno(String servizioControllo, String servizio, String data, String fineTurno){
        //aggiorna il servizio
        String query = "UPDATE Turno" +
                " SET Servizio = " + "'" + servizioControllo + "', isStraordinario = 1" +
                " WHERE Servizio = " + "'" + servizio + "'"+
                " AND time(Data_Fine) = " + "time('" + fineTurno + "')" +
                " AND date(Data_Inizio) = date('" + data + "')" +
                " LIMIT 1";
        try {
            Connection conn = connectToDatabase();
            Statement st = ProcessoDBMS.connectToDatabase().prepareStatement(query);
            int result = st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            ErroreDBMS.erroreGenericoDBMS(e);
        }
    }


    public static ArrayList<String> recuperaEmailAmministratori(){
        ArrayList<String> listaEmail = new ArrayList<>();
        try {
            Connection conn = connectToDatabase();
            String query = "SELECT Email" +
                    " FROM Impiegato" +
                    " WHERE is_Admin = 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                listaEmail.add(rs.getString("Email"));
            }
            closeConnectionToDatabase(conn);
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
        return listaEmail;
    }

    public static ArrayList<Impiegato> recuperaImpiegatiSenzaTurni(String data, String time){
        ArrayList<Impiegato> lista = new ArrayList<>();
        try {
            Connection conn = connectToDatabase();
            String query = "SELECT I.matricola, I.ref_ruolo" +
            " FROM Impiegato I" +
            " WHERE I.matricola NOT IN " +
                    "( " +
                    "SELECT I2.matricola" +
                    " FROM Impiegato I2, Turno T, Astensione A" +
                    " WHERE I2.matricola = T.ref_impiegato AND A.ref_impiegato = I2.matricola" +
                    " AND time(T.data_fine) = time('14:00:00')" +
                    " AND '2023-01-23' BETWEEN date(T.data_inizio) AND date(T.data_fine)" +
                    " AND '2023-01-23' BETWEEN date(A.data_inizio) AND date(A.data_fine)" +
                    " )";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                lista.add(new Impiegato(rs.getInt("Matricola"), rs.getInt("Ref_Ruolo")));
            }
            closeConnectionToDatabase(conn);
        } catch (SQLException e) {
            ErroreDBMS.erroreGenericoDBMS(e);
            e.printStackTrace();
        }
        return lista;
    }

    public static void inserisciTurnoStraordinaio(String data,  String tempo, int matricola, String servizio){
        // tempo può avere 20:00:00 o 14:00:00
        LocalDate dataTurno = LocalDate.parse(data);
        LocalTime time = LocalTime.parse(tempo);

        String dataFineTurno = dataTurno.toString() + " " + time.toString();
        String dataInizioTurno = dataTurno.toString() + " " + time.minusHours(6).toString();

        try {
            Connection conn = connectToDatabase();
            String query = "INSERT INTO Turno(Servizio, Data_Inizio, Data_Fine, Ref_Impiegato, isStraordinario)" +
                    " VALUES(?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, servizio);
            stm.setString(2, dataInizioTurno);
            stm.setString(3, dataFineTurno);
            stm.setInt(4, matricola);
            stm.setInt(5, 1);
            stm.executeUpdate();
            connectToDatabase().close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErroreDBMS.erroreGenericoDBMS(e);
        }
    }
}

    /*
    public ArrayList<Turno> recuperaTurniCorrenti(String data){

   }

   public boolean spostaTurno(Turno turno, int matricola){

   }

*/


