package com.example.movie_ticket.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PaymentRow {
    final StringProperty customerId;
    final StringProperty ticketID;
    final StringProperty price;

    public PaymentRow(String customerId, String ticketID, String price) {
        this.customerId = new SimpleStringProperty(customerId);
        this.ticketID = new SimpleStringProperty(ticketID);
        this.price = new SimpleStringProperty(price);
    }

    public StringProperty customerIdProperty() { return customerId; }
    public StringProperty ticketIdProperty() { return ticketID; }
    public StringProperty priceProperty() { return price; }
}