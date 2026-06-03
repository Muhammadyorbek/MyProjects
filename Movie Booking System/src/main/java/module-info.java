module com.example.movie_ticket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;



    opens com.example.movie_ticket to javafx.fxml;
    opens com.example.movie_ticket.ui to javafx.graphics, javafx.fxml;
    opens com.example.movie_ticket.ui.screens to javafx.graphics, javafx.fxml;

    exports com.example.movie_ticket;
    opens com.example.movie_ticket.ui.screens.admin to javafx.fxml, javafx.graphics;
}