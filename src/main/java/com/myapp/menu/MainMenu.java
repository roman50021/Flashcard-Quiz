package com.myapp.menu;

import com.myapp.menu.io.ConsoleIO;
import com.myapp.model.Deck;
import com.myapp.service.DeckService;
import com.myapp.exp.ValidationException;

public class MainMenu {
    private final ConsoleIO io;
    private final DeckService deckService;

    public MainMenu(ConsoleIO io, DeckService deckService) {
        this.io = io; this.deckService = deckService;
    }

    public void run() {
        while (true) {
            io.println("\n=== Flashcards ===");
            io.println("1) Колоди (управління)");
            io.println("0) Вихід");
            String cmd = io.readLine("> ");
            switch (cmd) {
                case "1" -> new DeckMenu(io, deckService).run();
                case "0" -> { io.println("Бувай!"); return; }
                default -> io.println("Невідома команда.");
            }
        }
    }

    private void createDeck() {
        String name = io.readNonEmpty("Назва колоди: ");
        try {
            Deck d = deckService.create(name);
            io.println("Створено: " + d);
        } catch (ValidationException e) {
            io.println("Помилка: " + e.getMessage());
        }
    }

    private void listDecks() {
        var all = deckService.list();
        if (all.isEmpty()) io.println("Поки що немає колод.");
        else all.forEach(x -> io.println(" - " + x));
    }
}
