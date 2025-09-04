package com.xiaoyumc.chinesename.db;

import com.xiaoyumc.chinesename.ChineseName;
import com.xiaoyumc.chinesename.config.ConfigManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DatabaseManager {
    private static HikariDataSource ds;

    public static void init(ChineseName plugin) {
        String type = ConfigManager.getSettings().getString("storage-type", "sqlite").toLowerCase();
        HikariConfig cfg = new HikariConfig();

        if ("mysql".equals(type)) {
            cfg.setJdbcUrl("jdbc:mysql://" +
                    ConfigManager.getSettings().getString("database_ip", "localhost") + ":" +
                    ConfigManager.getSettings().getInt("database_port", 3306) + "/" +
                    ConfigManager.getSettings().getString("database_name", "minecraft") +
                    "?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8");
            cfg.setUsername(ConfigManager.getSettings().getString("database_user", "root"));
            cfg.setPassword(ConfigManager.getSettings().getString("database_password", ""));
        } else {
            // SQLite
            cfg.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/chinesename.db");
        }

        ds = new HikariDataSource(cfg);
        createTable();
    }

    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS cn_names (uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(255))";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void shutdown() {
        if (ds != null) ds.close();
    }
}