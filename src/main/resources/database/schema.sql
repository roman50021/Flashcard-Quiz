PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS decks (
  id       INTEGER PRIMARY KEY AUTOINCREMENT,
  name     TEXT NOT NULL UNIQUE,
  created_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS cards (
  id             INTEGER PRIMARY KEY AUTOINCREMENT,
  deck_id        INTEGER NOT NULL,
  question       TEXT NOT NULL,
  answer         TEXT NOT NULL,
  ease           INTEGER NOT NULL DEFAULT 250,
  interval_days  INTEGER NOT NULL DEFAULT 0,
  next_review_date TEXT,
  FOREIGN KEY (deck_id) REFERENCES decks(id) ON DELETE CASCADE
);
