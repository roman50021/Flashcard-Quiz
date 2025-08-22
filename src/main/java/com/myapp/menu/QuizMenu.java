package com.myapp.menu;

import com.myapp.menu.io.ConsoleIO;
import com.myapp.model.Card;
import com.myapp.model.Deck;
import com.myapp.service.QuizService;

import java.util.List;

public class QuizMenu {
    private final ConsoleIO io;
    private final Deck deck;
    private final QuizService quizService;

    public QuizMenu(ConsoleIO io, Deck deck) {
        this.io = io;
        this.deck = deck;
        this.quizService = ServiceLocator.quizService(); // дістанемо з локатора
    }

    public void run() {
        int limit = askLimit();
        List<Card> batch = quizService.pickForToday(deck.getId(), limit);
        if (batch.isEmpty()) {
            io.println("Сьогодні карток для повторення немає. Можеш додати нові!");
            return;
        }

        io.println("Починаємо! Карток у сесії: " + batch.size());
        int correct = 0, total = 0;

        for (Card c : batch) {
            io.println("\nQ: " + c.getQuestion());
            String ans = io.readLine("Відповідь: ");
            boolean ok = quizService.applyAnswer(deck.getId(), c, ans);
            total++;
            if (ok) {
                correct++;
                io.println("Вірно!");
            } else {
                io.println("Невірно. Правильна відповідь: " + c.getAnswer());
            }
        }
        io.println("\nГотово. Результат: " + correct + "/" + total);
    }

    private int askLimit() {
        String s = io.readLine("Скільки карток у сесії? (Enter = 10): ");
        if (s == null || s.isBlank()) return 10;
        try { return Math.max(1, Integer.parseInt(s.trim())); }
        catch (NumberFormatException e) { return 10; }
    }
}
