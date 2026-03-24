package com.pagora.migrator.dialect;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DialectFactory {

    private final Map<String, DatabaseDialect> dialectMap;

    // Spring, DatabaseDialect interface'ini implemente eden tüm sınıfları bu List'e otomatik doldurur.
    public DialectFactory(List<DatabaseDialect> dialects) {
        this.dialectMap = dialects.stream()
                .collect(Collectors.toMap(
                        dialect -> dialect.getDbType().toUpperCase(),
                        dialect -> dialect
                ));
    }

    public DatabaseDialect getDialect(String dbType) {
        DatabaseDialect dialect = dialectMap.get(dbType.toUpperCase());
        if (dialect == null) {
            throw new IllegalArgumentException("Desteklenmeyen veritabanı tipi: " + dbType);
        }
        return dialect;
    }
}