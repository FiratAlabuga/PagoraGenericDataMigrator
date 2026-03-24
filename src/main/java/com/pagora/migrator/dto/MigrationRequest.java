package com.pagora.migrator.dto;

import lombok.Data;

@Data
public class MigrationRequest {
    private DbConfig source;
    private DbConfig target;
    private MigrationSettings settings;
}
