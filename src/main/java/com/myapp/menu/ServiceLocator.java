package com.myapp.menu;

import com.myapp.service.CardService;
import com.myapp.service.QuizService;

public final class ServiceLocator {
    private static CardService CARD_SERVICE;
    private static QuizService QUIZ_SERVICE;

    private ServiceLocator() {}

    public static void setCardService(CardService s) { CARD_SERVICE = s; }
    public static CardService cardService() { return CARD_SERVICE; }

    public static void setQuizService(QuizService s) { QUIZ_SERVICE = s; }
    public static QuizService quizService() { return QUIZ_SERVICE; }
}
