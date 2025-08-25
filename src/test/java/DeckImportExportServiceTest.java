import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myapp.model.Card;
import com.myapp.model.Deck;
import com.myapp.repo.CardRepository;
import com.myapp.repo.DeckRepository;
import com.myapp.service.DeckImportExportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DeckImportExportServiceTest {

    File tmp;

    @AfterEach
    void cleanup() {
        if (tmp != null && tmp.exists()) tmp.delete();
    }

    @Test
    void export_writesJsonFile() throws IOException {
        DeckRepository deckRepo = mock(DeckRepository.class);
        CardRepository cardRepo = mock(CardRepository.class);
        DeckImportExportService io = new DeckImportExportService(deckRepo, cardRepo);

        Deck deck = new Deck(1L, "Exported", LocalDateTime.now());
        when(deckRepo.findById(1L)).thenReturn(Optional.of(deck));

        Card c = Card.builder().id(11L).deckId(1L).question("Q").answer("A")
                .ease(250).intervalDays(0).nextReviewDate(LocalDate.now()).build();
        when(cardRepo.findByDeckId(1L)).thenReturn(List.of(c));

        tmp = File.createTempFile("deck-", ".json");
        io.exportDeck(1L, tmp);

        assertThat(tmp).exists().isFile();
        assertThat(tmp.length()).isGreaterThan(10);
    }

    @Test
    void import_readsJsonAndCreatesDeckAndCards() throws Exception {
        // підготуємо JSON дамп самостійно (так само як сервіс)
        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
        var dump = new DeckImportExportService.DeckDump();
        dump.id = 77L;
        dump.name = "My Deck";
        dump.createdAt = LocalDateTime.now().minusDays(2);
        dump.cards = List.of(
                Card.builder().question("Q1").answer("A1").ease(250).intervalDays(0).nextReviewDate(null).build(),
                Card.builder().question("Q2").answer("A2").ease(250).intervalDays(3).nextReviewDate(LocalDate.now()).build()
        );
        tmp = File.createTempFile("import-", ".json");
        om.writerWithDefaultPrettyPrinter().writeValue(tmp, dump);

        // моки
        DeckRepository deckRepo = mock(DeckRepository.class);
        CardRepository cardRepo = mock(CardRepository.class);
        when(deckRepo.findByName("My Deck")).thenReturn(Optional.empty());
        when(deckRepo.save(any())).thenAnswer(inv -> {
            Deck d = inv.getArgument(0);
            d.setId(10L);
            return d;
        });

        DeckImportExportService io = new DeckImportExportService(deckRepo, cardRepo);

        Deck created = io.importDeck(tmp);

        assertThat(created.getId()).isEqualTo(10L);
        verify(deckRepo).save(any(Deck.class));
        // двічі збережено картки
        verify(cardRepo, times(2)).save(eq(10L), any(Card.class));
    }
}
