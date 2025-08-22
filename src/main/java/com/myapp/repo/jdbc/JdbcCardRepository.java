package com.myapp.repo.jdbc;

import com.myapp.model.Card;
import com.myapp.repo.CardRepository;
import com.myapp.util.ConnectionProvider;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCardRepository implements CardRepository {
    private final ConnectionProvider cp;

    public JdbcCardRepository(ConnectionProvider cp) { this.cp = cp; }

    @Override
    public Card save(Long deckId, Card card) {
        if (card.getId() == null) {
            String sql = "INSERT INTO cards(deck_id,question,answer,ease,interval_days,next_review_date) VALUES(?,?,?,?,?,?)";
            try (Connection c = cp.get();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, deckId);
                ps.setString(2, card.getQuestion());
                ps.setString(3, card.getAnswer());
                ps.setInt(4, card.getEase());
                ps.setInt(5, card.getIntervalDays());
                if (card.getNextReviewDate() == null) ps.setNull(6, Types.VARCHAR);
                else ps.setString(6, card.getNextReviewDate().toString());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) card.setId(rs.getLong(1));
                }
                return card;
            } catch (SQLException e) { throw new RuntimeException("Insert card failed", e); }
        } else {
            String sql = "UPDATE cards SET question=?, answer=?, ease=?, interval_days=?, next_review_date=? WHERE id=? AND deck_id=?";
            try (Connection c = cp.get(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, card.getQuestion());
                ps.setString(2, card.getAnswer());
                ps.setInt(3, card.getEase());
                ps.setInt(4, card.getIntervalDays());
                if (card.getNextReviewDate() == null) ps.setNull(5, Types.VARCHAR);
                else ps.setString(5, card.getNextReviewDate().toString());
                ps.setLong(6, card.getId());
                ps.setLong(7, deckId);
                ps.executeUpdate();
                return card;
            } catch (SQLException e) { throw new RuntimeException("Update card failed", e); }
        }
    }

    @Override
    public Optional<Card> findById(Long deckId, Long cardId) {
        String sql = "SELECT * FROM cards WHERE id=? AND deck_id=?";
        try (Connection c = cp.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cardId); ps.setLong(2, deckId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Find card failed", e); }
    }

    @Override
    public List<Card> findByDeckId(Long deckId) {
        String sql = "SELECT * FROM cards WHERE deck_id=? ORDER BY id";
        try (Connection c = cp.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, deckId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Card> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) { throw new RuntimeException("Find cards failed", e); }
    }

    @Override
    public void deleteById(Long deckId, Long cardId) {
        String sql = "DELETE FROM cards WHERE id=? AND deck_id=?";
        try (Connection c = cp.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cardId); ps.setLong(2, deckId); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Delete card failed", e); }
    }

    private static Card map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long deckId = rs.getLong("deck_id");
        String q = rs.getString("question");
        String a = rs.getString("answer");
        int ease = rs.getInt("ease");
        int interval = rs.getInt("interval_days");
        String next = rs.getString("next_review_date");
        return new Card(id, deckId, q, a, ease, interval, next == null ? null : LocalDate.parse(next));
    }
}
