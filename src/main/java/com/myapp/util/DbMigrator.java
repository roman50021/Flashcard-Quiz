package com.myapp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DbMigrator {
    public static void migrate(ConnectionProvider cp, String classpathSql) {
        try (Connection c = cp.get(); Statement st = c.createStatement()) {
            String raw = new BufferedReader(
                    new InputStreamReader(
                            DbMigrator.class.getResourceAsStream(classpathSql.replace("classpath:", "")),
                            StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            for (String part : raw.split(";")) {
                String sql = part.trim();
                if (sql.isEmpty()) continue;
                st.execute(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException("DB migration failed", e);
        }
    }
}
