package com.myapp.model;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = {"id","deckId","question"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Card {
    @EqualsAndHashCode.Include
    private Long id;
    private Long deckId;
    private String question;
    private String answer;
    private int ease;
    private int intervalDays;
    private LocalDate nextReviewDate;

    public static Card newCard(Long deckId, String q, String a) {
        return Card.builder()
                .deckId(deckId)
                .question(q)
                .answer(a)
                .ease(250)
                .intervalDays(0)
                .build();
    }
}
