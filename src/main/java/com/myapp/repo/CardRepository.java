package com.myapp.repo;

import com.myapp.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardRepository  {
    Card save(Long deckId, Card card);
    Optional<Card> findById(Long deckId, Long cardId);
    List<Card> findByDeckId(Long deckId);
    void deleteById(Long deckId, Long cardId);
}
