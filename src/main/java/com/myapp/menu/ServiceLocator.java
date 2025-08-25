package com.myapp.menu;

import com.myapp.service.CardService;
import com.myapp.service.DeckImportExportService;
import com.myapp.service.QuizService;

public final class ServiceLocator {
    private static CardService CARD_SERVICE;
    private static QuizService QUIZ_SERVICE;
    private static DeckImportExportService DECK_IO;

    private ServiceLocator() {}

    public static void setCardService(CardService s) { CARD_SERVICE = s; }
    public static CardService cardService() { return CARD_SERVICE; }

    public static void setQuizService(QuizService s) { QUIZ_SERVICE = s; }
    public static QuizService quizService() { return QUIZ_SERVICE; }

    public static void setDeckIo(DeckImportExportService s){ DECK_IO = s; }
    public static DeckImportExportService deckIo(){ return DECK_IO; }
}
