package com.myapp.repo;

import com.myapp.model.Deck;

import java.util.List;
import java.util.Optional;

public interface DeckRepository {
    Deck save(Deck deck);
    Optional<Deck> findById(long id);
    Optional<Deck> findByName(String name);
    List<Deck> findAll();
    void deleteById(Long id);
}
