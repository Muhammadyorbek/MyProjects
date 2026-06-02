package com.example.movie_ticket.ui.screens;

import com.example.movie_ticket.ui.screens.admin.CinemaBookingBridge;

import java.util.ArrayList;
import java.util.List;

public class BasketManager {

    private static final BasketManager INSTANCE = new BasketManager();
    public static BasketManager getInstance() { return INSTANCE; }

    private final List<CinemaBookingBridge.TicketItem> items = new ArrayList<>();
    private Runnable onChanged;

    public void setOnChanged(Runnable r) { this.onChanged = r; }

    public void add(CinemaBookingBridge.TicketItem item) {
        items.add(item);
        notifyChanged();
    }

    public void remove(CinemaBookingBridge.TicketItem item) {
        items.remove(item);
        notifyChanged();
    }

    public void updateOrAdd(CinemaBookingBridge.TicketItem newItem) {
        for (int i = 0; i < items.size(); i++) {
            CinemaBookingBridge.TicketItem existing = items.get(i);
            if (existing.movie.getMovieId() == newItem.movie.getMovieId()
                    && existing.show.id == newItem.show.id) {
                items.set(i, newItem);
                notifyChanged();
                return;
            }
        }
        add(newItem);
    }

    public void clear() {
        items.clear();
        notifyChanged();
    }

    public List<CinemaBookingBridge.TicketItem> getItems() {
        return new ArrayList<>(items);
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public double getTotal() {
        return items.stream()
                .mapToDouble(CinemaBookingBridge.TicketItem::total)
                .sum();
    }

    public boolean containsMovie(int movieId) {
        return items.stream()
                .anyMatch(t -> t.movie.getMovieId() == movieId);
    }


    public CinemaBookingBridge.TicketItem getByMovieId(int movieId) {
        return items.stream()
                .filter(t -> t.movie.getMovieId() == movieId)
                .findFirst()
                .orElse(null);
    }

    private void notifyChanged() {
        if (onChanged != null) onChanged.run();
    }
}