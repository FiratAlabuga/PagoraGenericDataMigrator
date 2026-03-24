package com.pagora.migrator.config;

import com.pagora.migrator.dto.DbConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DynamicDataSourceManager {

    /**
     * Gelen ayarlara göre çalışma anında yeni bir veritabanı bağlantı havuzu oluşturur.
     */
    public DataSource createDataSource(DbConfig config) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(config.getJdbcUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        dataSource.setDriverClassName(getDriverClassName(config.getType()));

        // Batch işleri için performansı artırmak adına pool ayarları
        dataSource.setMinimumIdle(2);
        dataSource.setMaximumPoolSize(10);
        dataSource.setConnectionTimeout(30000); // 30 saniye

        return dataSource;
    }

    private String getDriverClassName(String type) {
        return switch (type.toUpperCase()) {
            case "MYSQL" -> "com.mysql.cj.jdbc.Driver";
            case "POSTGRES" -> "org.postgresql.Driver";
            case "SQLITE" -> "org.sqlite.JDBC";
            default -> throw new IllegalArgumentException("Desteklenmeyen veritabanı tipi: " + type);
        };
    }
}