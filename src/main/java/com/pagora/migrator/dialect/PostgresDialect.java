package com.pagora.migrator.dialect;

import com.pagora.migrator.model.ColumnMetadata;
import com.pagora.migrator.model.TableMetadata;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.pagora.migrator.model.UniversalDataType.*;

@Component
public class PostgresDialect implements DatabaseDialect {

    @Override
    public String getDbType() {
        return "POSTGRES";
    }

    @Override
    public TableMetadata getTableMetadata(DataSource dataSource, String tableName) {
        // Kaynak Postgres ise burası çalışır (Şimdilik MySQL'den okuyacağımız için boş bırakabiliriz)
        throw new UnsupportedOperationException("Postgres'ten okuma henüz implemente edilmedi.");
    }

    @Override
    public String generateCreateTableSql(TableMetadata metadata) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(metadata.getTableName()).append(" (\n");

        for (int i = 0; i < metadata.getColumns().size(); i++) {
            ColumnMetadata col = metadata.getColumns().get(i);
            sql.append("    ").append(col.getName()).append(" ");
            sql.append(mapFromUniversalType(col));

            if (!col.isNullable()) {
                sql.append(" NOT NULL");
            }

            if (i < metadata.getColumns().size() - 1) {
                sql.append(",\n");
            }
        }
        sql.append("\n);");
        return sql.toString();
    }

    private String mapFromUniversalType(ColumnMetadata col) {
        return switch (col.getUniversalType()) {
            case STRING -> col.getSize() > 255 ? "TEXT" : "VARCHAR(" + col.getSize() + ")";
            case INTEGER -> "INTEGER";
            case LONG -> "BIGINT";
            case DATE -> "DATE";
            case TIMESTAMP -> "TIMESTAMP";
            default -> "TEXT"; // Güvenli liman
        };
    }
}