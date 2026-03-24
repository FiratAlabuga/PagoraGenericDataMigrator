package com.pagora.migrator.dialect;

import com.pagora.migrator.model.ColumnMetadata;
import com.pagora.migrator.model.TableMetadata;
import com.pagora.migrator.model.UniversalDataType;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
public class MySqlDialect implements DatabaseDialect {

    @Override
    public String getDbType() {
        return "MYSQL";
    }

    @Override
    public TableMetadata getTableMetadata(DataSource dataSource, String tableName) {
        TableMetadata metadata = new TableMetadata();
        metadata.setTableName(tableName);

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet columns = dbMetaData.getColumns(null, null, tableName, null);

            while (columns.next()) {
                ColumnMetadata col = new ColumnMetadata();
                col.setName(columns.getString("COLUMN_NAME"));
                String typeName = columns.getString("TYPE_NAME");
                col.setType(typeName);
                col.setSize(columns.getInt("COLUMN_SIZE"));
                col.setNullable(columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                col.setComment(columns.getString("REMARKS"));

                // MySQL tipini evrensel tipe çevir
                col.setUniversalType(mapToUniversalType(typeName));

                metadata.getColumns().add(col);
            }
        } catch (Exception e) {
            throw new RuntimeException("MySQL'den metadata okunamadı: " + tableName, e);
        }
        return metadata;
    }

    @Override
    public String generateCreateTableSql(TableMetadata metadata) {
        // Hedef MySQL ise burası çalışır (Şimdilik sadece Postgres'e yazacağımız için boş bırakabiliriz)
        throw new UnsupportedOperationException("MySQL için Create Table henüz implemente edilmedi.");
    }

    private UniversalDataType mapToUniversalType(String mysqlType) {
        mysqlType = mysqlType.toUpperCase();
        if (mysqlType.contains("VARCHAR") || mysqlType.contains("TEXT") || mysqlType.contains("CHAR")) return UniversalDataType.STRING;
        if (mysqlType.contains("BIGINT")) return UniversalDataType.LONG;
        if (mysqlType.contains("INT")) return UniversalDataType.INTEGER;
        if (mysqlType.contains("DATE")) return UniversalDataType.DATE;
        if (mysqlType.contains("TIMESTAMP") || mysqlType.contains("DATETIME")) return UniversalDataType.TIMESTAMP;
        return UniversalDataType.UNKNOWN;
    }
}