package com.myapp.repo.jdbc;

import com.myapp.model.Deck;
import com.myapp.repo.DeckRepository;
import com.myapp.util.ConnectionProvider;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcDeckRepository implements DeckRepository {
    private final ConnectionProvider cp;

    public JdbcDeckRepository(ConnectionProvider cp) { this.cp = cp; }

    @Override
    public Deck save(Deck deck) {
        if (deck.getId() == null) {
            String sql = "INSERT INTO decks(name, created_at) VALUES(?, ?)";
            try (Connection c = cp.get();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, deck.getName());
                ps.setString(2, deck.getCreatedAt().toString());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) deck.setId(rs.getLong(1));
                }
                return deck;
            } catch (SQLException e) {
                throw new RuntimeException("Insert deck failed", e);
            }
        } else {
            String sql = "UPDATE decks SET name=? WHERE id=?";
            try (Connection c = cp.get(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, deck.getName());
                ps.setLong(2, deck.getId());
                ps.executeUpdate();
                return deck;
            } catch (SQLException e) {
                throw new RuntimeException("Update deck failed", e);
            }
        }
    }

    @Override
    public Optional<Deck> findById(long id) {
        String sql = "SELECT id,name,created_at FROM decks WHERE id=?";
        try (Connection c = cp.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find deck failed", e);
        }
    }

    @Override
    public Optional<Deck> findByName(String name) {
        String sql = "SELECT id,name,created_at FROM decks WHERE name=?";
        try (Connection c = cp.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find deck by name failed", e);
        }
    }

    @Override
    public List<Deck> findAll() {
        String sql = "SELECT id,name,created_at FROM decks ORDER BY id";
        try (Connection c = cp.get(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Deck> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Find all decks failed", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM decks WHERE id=?";
        try (Connection c = cp.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete deck failed", e);
        }
    }

    private static Deck map(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"));
        return new Deck(id, name, createdAt);
    }
}
