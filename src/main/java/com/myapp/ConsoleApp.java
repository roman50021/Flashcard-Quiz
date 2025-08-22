package com.myapp;

import com.myapp.config.AppConfig;
import com.myapp.menu.MainMenu;
import com.myapp.menu.ServiceLocator;
import com.myapp.menu.io.ConsoleIO;
import com.myapp.repo.CardRepository;
import com.myapp.repo.DeckRepository;
import com.myapp.repo.jdbc.JdbcCardRepository;
import com.myapp.repo.jdbc.JdbcDeckRepository;
import com.myapp.service.CardService;
import com.myapp.service.DeckService;
import com.myapp.service.QuizService;
import com.myapp.service.ValidationService;
import com.myapp.util.ConnectionProvider;
import com.myapp.util.DbMigrator;

public class ConsoleApp {
    public static void main(String[] args) {
        AppConfig cfg = AppConfig.load();

        ConnectionProvider cp = new ConnectionProvider(cfg.jdbcUrl());
        if (cfg.autoload()) {
            DbMigrator.migrate(cp, cfg.jdbcInitScript());
        }

        DeckRepository deckRepo = new JdbcDeckRepository(cp);
        CardRepository cardRepo = new JdbcCardRepository(cp);

        ValidationService v = new ValidationService();
        DeckService deckService = new DeckService(deckRepo, v);
        CardService cardService = new CardService(cardRepo, v);
        QuizService quizService = new QuizService(cardRepo);

        ServiceLocator.setCardService(cardService);
        ServiceLocator.setQuizService(quizService);

        ConsoleIO io = new ConsoleIO();
        new MainMenu(io, deckService).run();
    }
}