package com.myapp.menu;

import com.myapp.exp.ValidationException;
import com.myapp.menu.io.ConsoleIO;
import com.myapp.model.Deck;
import com.myapp.service.DeckService;

import java.util.List;

public class DeckMenu {
    private final ConsoleIO io;
    private final DeckService deckService;

    public DeckMenu(ConsoleIO io, DeckService deckService) {
        this.io = io; this.deckService = deckService;
    }

    public void run() {
        while (true) {
            io.println("\n=== Колоди ===");
            var decks = deckService.list();
            printDecks(decks);
            io.println("1) Створити колоду");
            io.println("2) Перейменувати колоду");
            io.println("3) Видалити колоду");
            io.println("4) Відкрити колоду (картки)");
            io.println("0) Назад");
            io.println("5) Почати квіз");
            io.println("6) Експорт колоди (JSON)");
            io.println("7) Імпорт колоди з JSON");
            String cmd = io.readLine("> ");
            switch (cmd) {
                case "1" -> createDeck();
                case "2" -> renameDeck(decks);
                case "3" -> deleteDeck(decks);
                case "4" -> openDeck(decks);
                case "5" -> startQuiz(decks);
                case "6" -> exportDeck(decks);
                case "7" -> importDeck();
                case "0" -> { return; }
                default -> io.println("Невідома команда.");
            }
        }
    }

    private void printDecks(List<Deck> decks) {
        if (decks.isEmpty()) io.println("(немає колод)");
        else decks.forEach(d -> io.println(" - [" + d.getId() + "] " + d.getName()));
    }

    private void createDeck() {
        String name = io.readNonEmpty("Назва: ");
        try {
            var d = deckService.create(name);
            io.println("Створено: " + d);
        } catch (ValidationException e) {
            io.println("Помилка: " + e.getMessage());
        }
    }

    private void renameDeck(List<Deck> decks) {
        if (decks.isEmpty()) { io.println("Немає колод."); return; }
        long id = parseId(io.readNonEmpty("ID колоди для перейменування: "));
        String newName = io.readNonEmpty("Нова назва: ");
        try {
            var d = deckService.rename(id, newName);
            io.println("Перейменовано: " + d);
        } catch (ValidationException e) {
            io.println("Помилка: " + e.getMessage());
        }
    }

    private void deleteDeck(List<Deck> decks) {
        if (decks.isEmpty()) { io.println("Немає колод."); return; }
        long id = parseId(io.readNonEmpty("ID колоди для видалення: "));
        deckService.delete(id);
        io.println("Видалено.");
    }

    private void openDeck(List<Deck> decks) {
        if (decks.isEmpty()) { io.println("Немає колод."); return; }
        long id = parseId(io.readNonEmpty("ID колоди: "));
        var deck = decks.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
        if (deck == null) { io.println("Колоду не знайдено."); return; }
        new CardMenu(io, deck).run(); // CardMenu — нижче
    }

    private void startQuiz(List<Deck> decks) {
        if (decks.isEmpty()) { io.println("Немає колод."); return; }
        long id = parseId(io.readNonEmpty("ID колоди: "));
        var deck = decks.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
        if (deck == null) { io.println("Колоду не знайдено."); return; }
        new QuizMenu(io, deck).run();
    }

    private long parseId(String s) {
        try { return Long.parseLong(s); }
        catch (NumberFormatException e) { throw new ValidationException("Некоректний ID."); }
    }

    private void exportDeck(List<Deck> decks) {
        if (decks.isEmpty()) { io.println("Немає колод."); return; }

        String sId = io.readNonEmpty("ID колоди для експорту: ");
        long id;
        try {
            id = parseId(sId);
        } catch (ValidationException ex) {
            io.println("Помилка: " + ex.getMessage());
            return;
        }

        var deck = decks.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
        if (deck == null) { io.println("Колоду не знайдено."); return; }

        String path = io.readNonEmpty("Файл (напр. ./data/deck-" + id + ".json): ");
        try {
            ServiceLocator.deckIo().exportDeck(id, new java.io.File(path));
            io.println("Експортовано до: " + path);
        } catch (Exception e) {
            io.println("Помилка експорту: " + e.getMessage());
        }
    }

    private void importDeck() {
        String path = io.readNonEmpty("Шлях до JSON: ");
        try {
            var d = ServiceLocator.deckIo().importDeck(new java.io.File(path));
            io.println("Імпортовано як: [" + d.getId() + "] " + d.getName());
        } catch (Exception e) { io.println("Помилка імпорту: " + e.getMessage()); }
    }

}
