package com.myapp.menu;

import com.myapp.exp.ValidationException;
import com.myapp.menu.io.ConsoleIO;
import com.myapp.model.Card;
import com.myapp.model.Deck;
import com.myapp.service.CardService;

public class CardMenu {
    private final ConsoleIO io;
    private final Deck deck;
    private final CardService cardService;

    public CardMenu(ConsoleIO io, Deck deck) {
        this.io = io; this.deck = deck;
        this.cardService = ServiceLocator.cardService();
    }

    public void run() {
        while (true) {
            io.println("\n=== Картки колоди [" + deck.getId() + "] " + deck.getName() + " ===");
            list();
            io.println("1) Додати картку");
            io.println("2) Редагувати картку");
            io.println("3) Видалити картку");
            io.println("0) Назад");
            String cmd = io.readLine("> ");
            switch (cmd) {
                case "1" -> add();
                case "2" -> edit();
                case "3" -> remove();
                case "0" -> { return; }
                default -> io.println("Невідома команда.");
            }
        }
    }

    private void list() {
        var cards = cardService.list(deck.getId());
        if (cards.isEmpty()) io.println("(немає карток)");
        else cards.forEach(c -> io.println(" - [" + c.getId() + "] " + c.getQuestion() + "  ->  " + c.getAnswer()));
    }

    private void add() {
        String q = io.readNonEmpty("Питання: ");
        String a = io.readNonEmpty("Відповідь: ");
        try {
            Card c = cardService.add(deck.getId(), q, a);
            io.println("Додано: [" + c.getId() + "]");
        } catch (ValidationException e) {
            io.println("Помилка: " + e.getMessage());
        }
    }

    private void edit() {
        String sid = io.readNonEmpty("ID картки: ");
        String q = io.readNonEmpty("Нове питання: ");
        String a = io.readNonEmpty("Нова відповідь: ");
        try {
            var c = cardService.update(deck.getId(), Long.parseLong(sid), q, a);
            io.println("Оновлено: [" + c.getId() + "]");
        } catch (Exception e) {
            io.println("Помилка: " + e.getMessage());
        }
    }

    private void remove() {
        String sid = io.readNonEmpty("ID картки: ");
        try {
            cardService.delete(deck.getId(), Long.parseLong(sid));
            io.println("Видалено.");
        } catch (Exception e) {
            io.println("Помилка: " + e.getMessage());
        }
    }
}
