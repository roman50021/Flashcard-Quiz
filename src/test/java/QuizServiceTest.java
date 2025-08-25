import com.myapp.model.Card;
import com.myapp.repo.CardRepository;
import com.myapp.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class QuizServiceTest {

    CardRepository repo;
    QuizService service;

    @BeforeEach
    void setUp() {
        repo = mock(CardRepository.class);
        service = new QuizService(repo);
    }

    @Test
    void pickForToday_prioritizesDueAndLimits() {
        Long deckId = 1L;
        LocalDate today = LocalDate.now();

        Card due1 = Card.builder().id(1L).deckId(deckId).question("d1").answer("a").nextReviewDate(today.minusDays(1)).build();
        Card due2 = Card.builder().id(2L).deckId(deckId).question("d2").answer("a").nextReviewDate(null).build();
        Card later = Card.builder().id(3L).deckId(deckId).question("l").answer("a").nextReviewDate(today.plusDays(3)).build();

        when(repo.findByDeckId(deckId)).thenReturn(List.of(due1, later, due2));

        List<Card> picked = service.pickForToday(deckId, 2);

        // обрано тільки 2, і всі вони з категорії "due"
        assertThat(picked).hasSize(2);
        assertThat(picked).extracting(Card::getId).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void applyAnswer_correct_increasesEaseAndInterval() {
        Long deckId = 1L;
        Card c = Card.builder()
                .id(5L).deckId(deckId).answer("Kyiv")
                .ease(250).intervalDays(0).build();

        boolean correct = service.applyAnswer(deckId, c, "  kyiv  ");

        assertThat(correct).isTrue();
        assertThat(c.getEase()).isEqualTo(270);             // +20
        assertThat(c.getIntervalDays()).isEqualTo(1);       // з 0 -> 1
        assertThat(c.getNextReviewDate()).isEqualTo(LocalDate.now().plusDays(1));
        verify(repo).save(deckId, c);
    }

    @Test
    void applyAnswer_wrong_resetsIntervalAndDecreasesEase() {
        Long deckId = 1L;
        Card c = Card.builder()
                .id(6L).deckId(deckId).answer("Kyiv")
                .ease(250).intervalDays(5).build();

        boolean correct = service.applyAnswer(deckId, c, "odessa");

        assertThat(correct).isFalse();
        assertThat(c.getEase()).isEqualTo(230);           // -20
        assertThat(c.getIntervalDays()).isEqualTo(1);     // reset
        assertThat(c.getNextReviewDate()).isEqualTo(LocalDate.now().plusDays(1));
        verify(repo).save(deckId, c);
    }
}
