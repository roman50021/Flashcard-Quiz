package com.myapp.service;

import com.myapp.exp.ValidationException;
import com.myapp.model.Card;
import com.myapp.repo.CardRepository;

import java.util.List;

public class CardService {
    private final CardRepository cardRepo;
    private final ValidationService v;

    public CardService(CardRepository cardRepo, ValidationService v) {
        this.cardRepo = cardRepo; this.v = v;
    }

    public Card add(Long deckId, String q, String a) {
        v.requireNonBlank(q, "Питання");
        v.requireNonBlank(a, "Відповідь");
        return cardRepo.save(deckId, Card.newCard(deckId, q.trim(), a.trim()));
    }

    public List<Card> list(Long deckId) {
        return cardRepo.findByDeckId(deckId);
    }

    public Card update(Long deckId, Long cardId, String q, String a) {
        v.requireNonBlank(q, "Питання");
        v.requireNonBlank(a, "Відповідь");
        var card = cardRepo.findById(deckId, cardId)
                .orElseThrow(() -> new ValidationException("Картку не знайдено: id=" + cardId));
        card.setQuestion(q.trim());
        card.setAnswer(a.trim());
        return cardRepo.save(deckId, card);
    }

    public void delete(Long deckId, Long cardId) {
        cardRepo.deleteById(deckId, cardId);
    }
}
