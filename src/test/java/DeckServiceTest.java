import com.myapp.exp.ValidationException;
import com.myapp.model.Deck;
import com.myapp.repo.DeckRepository;
import com.myapp.service.DeckService;
import com.myapp.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeckServiceTest {

    DeckRepository repo;
    ValidationService v;
    DeckService service;

    @BeforeEach
    void setUp() {
        repo = mock(DeckRepository.class);
        v = new ValidationService();
        service = new DeckService(repo, v);
    }

    @Test
    void create_ok_savesDeck() {
        when(repo.findByName("Java")).thenReturn(Optional.empty());
        // емуляція присвоєння ідентифікатора репозиторієм
        when(repo.save(any())).thenAnswer(inv -> {
            Deck d = inv.getArgument(0);
            d.setId(1L);
            return d;
        });

        Deck created = service.create(" Java "); // перевіримо trim

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getName()).isEqualTo("Java");
        verify(repo).save(any());
    }

    @Test
    void create_blank_throws() {
        assertThatThrownBy(() -> service.create("  "))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Назва колоди");
        verifyNoInteractions(repo);
    }

    @Test
    void create_duplicate_throws() {
        when(repo.findByName("Java")).thenReturn(Optional.of(
                new Deck(10L, "Java", LocalDateTime.now())
        ));

        assertThatThrownBy(() -> service.create("Java"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("вже існує");
        verify(repo, never()).save(any());
    }

    @Test
    void rename_ok_updatesDeck() {
        when(repo.findByName("New")).thenReturn(Optional.empty());
        when(repo.findById(5L)).thenReturn(Optional.of(new Deck(5L, "Old", LocalDateTime.now())));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Deck renamed = service.rename(5L, " New ");

        assertThat(renamed.getName()).isEqualTo("New");
        verify(repo).save(any(Deck.class));
    }

    @Test
    void rename_conflict_throws() {
        when(repo.findByName("Busy")).thenReturn(Optional.of(new Deck(9L, "Busy", LocalDateTime.now())));

        assertThatThrownBy(() -> service.rename(5L, "Busy"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("вже зайнята");
    }

    @Test
    void delete_callsRepository() {
        service.delete(7L);
        verify(repo).deleteById(7L);
    }
}
