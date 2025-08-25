package com.myapp.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myapp.exp.ValidationException;
import com.myapp.model.Card;
import com.myapp.model.Deck;
import com.myapp.repo.CardRepository;
import com.myapp.repo.DeckRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class DeckImportExportService {
    private final DeckRepository deckRepo;
    private final CardRepository cardRepo;
    private final ObjectMapper om = new ObjectMapper();

    public DeckImportExportService(DeckRepository deckRepo, CardRepository cardRepo) {
        this.deckRepo = deckRepo; this.cardRepo = cardRepo;
        om.setVisibility(om.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void exportDeck(long deckId, File file) {
        Deck deck = deckRepo.findById(deckId)
                .orElseThrow(() -> new ValidationException("Колоду не знайдено: id=" + deckId));
        DeckDump dump = new DeckDump();
        dump.id = deck.getId();
        dump.name = deck.getName();
        dump.createdAt = deck.getCreatedAt();
        dump.cards = cardRepo.findByDeckId(deckId);
        try {
            file.getParentFile().mkdirs();
            om.writerWithDefaultPrettyPrinter().writeValue(file, dump);
        } catch (IOException e) {
            throw new RuntimeException("Не вдалось записати файл: " + file, e);
        }
    }

    public Deck importDeck(File file) {
        DeckDump dump;
        try { dump = om.readValue(file, DeckDump.class); }
        catch (IOException e) { throw new RuntimeException("Не вдалось прочитати файл: " + file, e); }

        String base = dump.name != null ? dump.name : "Imported deck";
        String name = base; int n = 1;
        while (deckRepo.findByName(name).isPresent()) { n++; name = base + " (" + n + ")"; }

        Deck created = deckRepo.save(Deck.builder().name(name).createdAt(LocalDateTime.now()).build());
        if (dump.cards != null) {
            for (Card c : dump.cards) {
                cardRepo.save(created.getId(), Card.builder()
                        .deckId(created.getId())
                        .question(c.getQuestion())
                        .answer(c.getAnswer())
                        .ease(c.getEase())
                        .intervalDays(c.getIntervalDays())
                        .nextReviewDate(c.getNextReviewDate())
                        .build());
            }
        }
        return created;
    }

    public static class DeckDump {
        public Long id;
        public String name;
        public LocalDateTime createdAt;
        public List<Card> cards;
    }
}
