package com.myapp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record AppConfig(
        String storage,
        String jdbcUrl,
        String jdbcInitScript,
        String filesDir,
        boolean autoload
) {
    public static AppConfig load() {
        Properties p = new Properties();
        try (InputStream in = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (in == null) throw new IllegalStateException("application.properties not found");
            p.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }

        return new AppConfig(
                subst(p.getProperty("storage", "jdbc")),
                subst(p.getProperty("jdbc.url")),
                subst(p.getProperty("jdbc.init-script")),
                subst(p.getProperty("files.dir", "./data")),
                Boolean.parseBoolean(subst(p.getProperty("autoload", "true")))
        );
    }

    private static String subst(String in) {
        if (in == null) return null;
        var m = java.util.regex.Pattern.compile("\\$\\{([^}]+)}").matcher(in);
        var sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            String val = System.getenv(key);
            if (val == null) val = System.getProperty(key);
            if (val == null) throw new IllegalStateException("No value for ${" + key + "}");
            m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(val));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}