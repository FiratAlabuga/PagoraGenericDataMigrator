package com.pagora.migrator.dto;

import lombok.Data;

@Data
public class MigrationSettings {
    private String tableName;
    private int batchSize = 10000; // Varsayılan olarak 10 bin
}