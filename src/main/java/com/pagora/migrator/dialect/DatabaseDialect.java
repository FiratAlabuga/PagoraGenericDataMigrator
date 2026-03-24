package com.pagora.migrator.dialect;

import com.pagora.migrator.model.TableMetadata;
import javax.sql.DataSource;

public interface DatabaseDialect {
    // Hangi veritabanı olduğunu belirtir (MYSQL, POSTGRES vs.)
    String getDbType();

    // Kaynak veritabanından tablonun şemasını okur
    TableMetadata getTableMetadata(DataSource dataSource, String tableName);

    // Hedef veritabanında tablo oluşturmak için gereken SQL'i üretir
    String generateCreateTableSql(TableMetadata metadata);
}