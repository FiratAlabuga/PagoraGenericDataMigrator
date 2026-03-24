package com.pagora.migrator.service;

import com.pagora.migrator.config.DynamicDataSourceManager;
import com.pagora.migrator.dialect.DatabaseDialect;
import com.pagora.migrator.dialect.DialectFactory;
import com.pagora.migrator.dto.MigrationRequest;
import com.pagora.migrator.model.TableMetadata;
import com.pagora.migrator.core.MigrationJobFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator; // <-- YENİ IMPORT
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService {

    private final DynamicDataSourceManager dataSourceManager;
    private final DialectFactory dialectFactory;
    private final MigrationJobFactory jobFactory;
    private final JobOperator jobOperator; // <-- JobLauncher yerine JobOperator geldi

    public void startMigrationProcess(MigrationRequest request) {
        // ... (Bağlantı kurma, Dialect seçme ve Tablo oluşturma kısımları aynı kalıyor) ...

        try {
            DataSource sourceDataSource = dataSourceManager.createDataSource(request.getSource());
            DataSource targetDataSource = dataSourceManager.createDataSource(request.getTarget());
            DatabaseDialect sourceDialect = dialectFactory.getDialect(request.getSource().getType());
            DatabaseDialect targetDialect = dialectFactory.getDialect(request.getTarget().getType());

            TableMetadata metadata = sourceDialect.getTableMetadata(sourceDataSource, request.getSettings().getTableName());
            String createTableSql = targetDialect.generateCreateTableSql(metadata);

            try (Connection conn = targetDataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSql);
            }

            // Job'ı oluştur
            String jobId = "migrationJob_" + metadata.getTableName() + "_" + UUID.randomUUID();
            Job migrationJob = jobFactory.createMigrationJob(
                    jobId, sourceDataSource, targetDataSource, metadata, request.getSettings().getBatchSize()
            );

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("jobId", jobId)
                    .addLong("startTime", System.currentTimeMillis())
                    .toJobParameters();

            // 5. Arka planda asenkron olarak çalıştır
            runJobAsynchronously(migrationJob, jobParameters);

        } catch (Exception e) {
            log.error("Migrasyon başlatılamadı: ", e);
            throw new RuntimeException("Süreç başlatılırken hata!", e);
        }
    }

    // @Async sayesinde bu metod yeni bir Thread'de çalışır, API hemen HTTP 200 döner.
    @Async
    protected void runJobAsynchronously(Job migrationJob, JobParameters jobParameters) {
        try {
            log.info("Spring Batch Job arka planda başlatılıyor: {}", jobParameters.getString("jobId"));
            // Spring Batch 6.0 Güncel Tetikleme Metodu
            jobOperator.start(migrationJob, jobParameters);
            log.info("Migrasyon Job'ı başarıyla tamamlandı!");
        } catch (Exception e) {
            log.error("Migrasyon sırasında hata oluştu: ", e);
        }
    }
}