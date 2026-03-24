package com.pagora.migrator.dto;

import lombok.Data;

@Data
public class DbConfig {
    private String type;      // Örn: "MYSQL", "POSTGRES", "SQLITE"
    private String jdbcUrl;   // Örn: "jdbc:mysql://localhost:3306/source_db"
    private String username;
    private String password;
}