import com.myapp.exp.ValidationException;
import com.myapp.model.Card;
import com.myapp.repo.CardRepository;
import com.myapp.service.CardService;
import com.myapp.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    CardRepository repo;
    ValidationService v;
    CardService service;

    @BeforeEach
    void setUp() {
        repo = mock(CardRepository.class);
        v = new ValidationService();
        service = new CardService(repo, v);
    }

    @Test
    void add_ok_trimsAndSaves() {
        when(repo.save(eq(1L), any(Card.class))).thenAnswer(inv -> {
            Card c = inv.getArgument(1);
            c.setId(100L);
            return c;
        });

        Card saved = service.add(1L, "  Q?  ", "  A  ");

        assertThat(saved.getId()).isEqualTo(100L);
        assertThat(saved.getQuestion()).isEqualTo("Q?");
        assertThat(saved.getAnswer()).isEqualTo("A");
        verify(repo).save(eq(1L), any(Card.class));
    }

    @Test
    void add_blank_throws() {
        assertThatThrownBy(() -> service.add(1L, " ", "A"))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> service.add(1L, "Q", " "))
                .isInstanceOf(ValidationException.class);
        verify(repo, never()).save(anyLong(), any());
    }

    @Test
    void update_ok_updatesExisting() {
        Card existing = Card.builder().id(5L).deckId(1L).question("Q").answer("A").build();
        when(repo.findById(1L, 5L)).thenReturn(Optional.of(existing));
        when(repo.save(eq(1L), any(Card.class))).thenAnswer(inv -> inv.getArgument(1));

        Card updated = service.update(1L, 5L, " new q ", " new a ");

        assertThat(updated.getQuestion()).isEqualTo("new q");
        assertThat(updated.getAnswer()).isEqualTo("new a");
        verify(repo).save(eq(1L), same(existing));
    }

    @Test
    void update_notFound_throws() {
        when(repo.findById(1L, 404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(1L, 404L, "q", "a"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("не знайдено");
    }

    @Test
    void delete_callsRepository() {
        service.delete(2L, 3L);
        verify(repo).deleteById(2L, 3L);
    }
}