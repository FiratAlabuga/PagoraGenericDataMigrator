package com.pagora.migrator.core;

import com.pagora.migrator.model.ColumnMetadata;
import com.pagora.migrator.model.TableMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MigrationJobFactory {

    private final JobRepository jobRepository;

    public Job createMigrationJob(String jobId,
                                  DataSource sourceDataSource,
                                  DataSource targetDataSource,
                                  TableMetadata metadata,
                                  int chunkSize) {

        JdbcTransactionManager transactionManager = new JdbcTransactionManager(targetDataSource);

        // Spring Batch 6.x Güncel Kullanımı
        Step step = new StepBuilder("migrationStep_" + metadata.getTableName(), jobRepository)
                .<Map<String, Object>, Map<String, Object>>chunk(chunkSize) // 1. Sadece boyutu veriyoruz
                .transactionManager(transactionManager)                     // 2. Transaction Manager ayrı eklendi
                .reader(buildReader(sourceDataSource, metadata.getTableName()))
                .writer(buildWriter(targetDataSource, metadata))
                .build();

        return new JobBuilder(jobId, jobRepository)
                .start(step)
                .build();
    }

    private JdbcCursorItemReader<Map<String, Object>> buildReader(DataSource dataSource, String tableName) {
        return new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                .name("dynamicCursorReader_" + tableName)
                .dataSource(dataSource)
                .sql("SELECT * FROM " + tableName)
                .rowMapper(new ColumnMapRowMapper())
                .fetchSize(10000)
                .build();
    }

    private JdbcBatchItemWriter<Map<String, Object>> buildWriter(DataSource dataSource, TableMetadata metadata) {
        String insertSql = generateInsertSql(metadata);
        log.info("Hedef için üretilen INSERT SQL: {}", insertSql);

        return new JdbcBatchItemWriterBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .sql(insertSql)
                .columnMapped()
                .build();
    }

    private String generateInsertSql(TableMetadata metadata) {
        String columns = metadata.getColumns().stream()
                .map(ColumnMetadata::getName)
                .collect(Collectors.joining(", "));

        String placeholders = metadata.getColumns().stream()
                .map(col -> ":" + col.getName())
                .collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s) VALUES (%s)", metadata.getTableName(), columns, placeholders);
    }
}