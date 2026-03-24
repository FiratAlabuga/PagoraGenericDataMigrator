package com.pagora.migrator.model;

public enum UniversalDataType {
    STRING,      // VARCHAR, TEXT
    INTEGER,     // INT, INTEGER
    LONG,        // BIGINT
    DECIMAL,     // DECIMAL, NUMERIC, DOUBLE
    BOOLEAN,     // BOOLEAN, TINYINT(1)
    DATE,        // DATE
    TIMESTAMP,   // DATETIME, TIMESTAMP
    BINARY,      // BLOB, BYTEA
    UNKNOWN      // Desteklenmeyen tipler için
}