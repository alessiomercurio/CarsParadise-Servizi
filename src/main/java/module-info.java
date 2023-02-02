module com.agmg.carsparadise {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.mail;


    opens com.agmg.carsparadise to javafx.fxml;
    exports com.agmg.carsparadise;
}