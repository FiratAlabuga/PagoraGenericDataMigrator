package com.pagora.migrator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnMetadata {
    private String name;
    private String type;             // Kaynak DB'deki orijinal tipi (örn: VARCHAR, INT)
    private int size;
    private String comment;          // Varsa kolon açıklaması
    private boolean isNullable;
    private boolean isPrimaryKey;

    // Generic tip eşleşmesi için eklediğimiz evrensel alan
    private UniversalDataType universalType;
}