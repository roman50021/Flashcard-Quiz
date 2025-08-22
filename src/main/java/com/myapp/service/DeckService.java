package com.myapp.service;

import com.myapp.exp.ValidationException;
import com.myapp.model.Deck;
import com.myapp.repo.DeckRepository;

import java.util.List;

public class DeckService {
    private final DeckRepository deckRepo;
    private final ValidationService v;

    public DeckService(DeckRepository deckRepo, ValidationService v) {
        this.deckRepo = deckRepo; this.v = v;
    }

    public Deck create(String rawName) {
        String name = rawName == null ? null : rawName.trim();
        v.requireNonBlank(name, "Назва колоди");

        deckRepo.findByName(name).ifPresent(d -> {
            throw new ValidationException("Колода з назвою '" + name + "' вже існує.");
        });

        return deckRepo.save(Deck.newDeck(name));
    }

    public List<Deck> list() { return deckRepo.findAll(); }

    public Deck rename(long id, String newName) {
        String name = newName == null ? null : newName.trim();
        v.requireNonBlank(name, "Нова назва");

        deckRepo.findByName(name).ifPresent(d -> {
            if (!d.getId().equals(id))
                throw new ValidationException("Назва '" + name + "' вже зайнята іншою колодою.");
        });

        var deck = deckRepo.findById(id)
                .orElseThrow(() -> new ValidationException("Колоду не знайдено: id=" + id));
        deck.setName(name);
        return deckRepo.save(deck);
    }

    public void delete(long id) { deckRepo.deleteById(id); }
}
