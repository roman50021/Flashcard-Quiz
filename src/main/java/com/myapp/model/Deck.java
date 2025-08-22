package com.myapp.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = {"id","name"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Deck {
    @EqualsAndHashCode.Include
    private Long id;                 // null для нової
    private String name;
    private LocalDateTime createdAt;

    public static Deck newDeck(String name) {
        return Deck.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
