package com.myapp.service;

import com.myapp.model.Card;
import com.myapp.repo.CardRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class QuizService {
    private final CardRepository cardRepo;

    public QuizService(CardRepository cardRepo) {
        this.cardRepo = cardRepo;
    }

    /** Обрати до N карток для сьогоднішнього повторення (спершу due, потім нові/інші). */
    public List<Card> pickForToday(Long deckId, int limit) {
        var today = LocalDate.now();
        var all = cardRepo.findByDeckId(deckId);

        var due = all.stream().filter(c ->
                        c.getNextReviewDate() == null || !c.getNextReviewDate().isAfter(today))
                .collect(Collectors.toList());

        var rest = new ArrayList<>(all);
        rest.removeAll(due);

        Collections.shuffle(due);
        Collections.shuffle(rest);

        var picked = new ArrayList<Card>(limit);
        for (var c : due) {
            if (picked.size() >= limit) break;
            picked.add(c);
        }
        for (var c : rest) {
            if (picked.size() >= limit) break;
            picked.add(c);
        }
        return picked;
    }

    /** Застосувати відповідь користувача, оновивши SRS-поля і зберігши картку. */
    public boolean applyAnswer(Long deckId, Card card, String userAnswer) {
        boolean correct = isCorrect(card.getAnswer(), userAnswer);
        var today = LocalDate.now();

        int ease = card.getEase();
        int interval = card.getIntervalDays();

        if (correct) {
            ease = Math.min(350, ease + 20);
            interval = (interval == 0) ? 1 : Math.max(1, (int)Math.round(interval * 1.7));
        } else {
            ease = Math.max(130, ease - 20);
            interval = 1;
        }
        card.setEase(ease);
        card.setIntervalDays(interval);
        card.setNextReviewDate(today.plusDays(interval));

        cardRepo.save(deckId, card);
        return correct;
    }

    private boolean isCorrect(String truth, String attempt) {
        if (truth == null) return attempt == null;
        if (attempt == null) return false;
        return norm(truth).equals(norm(attempt));
    }

    private String norm(String s) {
        return s.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
