package com.pagora.migrator.service;

import com.pagora.migrator.dto.MigrationStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.repository.JobRepository; // <-- JobExplorer yerine JobRepository geldi
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MigrationTrackerService {

    private final JobRepository jobRepository; // <-- Sadece JobRepository enjekte ediyoruz

    public MigrationStatusResponse getStatus(String jobId) {

        // Artık tüm okuma işlemlerini de JobRepository üzerinden yapıyoruz
        List<JobInstance> instances = jobRepository.getJobInstances(jobId, 0, 1);

        if (instances.isEmpty()) {
            throw new RuntimeException("Bu ID ile bir migrasyon işlemi bulunamadı: " + jobId);
        }

        JobInstance instance = instances.get(0);

        // JobExecution'ları da artık repository üzerinden çekebiliyoruz
        List<JobExecution> executions = jobRepository.getJobExecutions(instance);

        if (executions.isEmpty()) {
            throw new RuntimeException("İşlem henüz başlamamış veya log kaydı yok.");
        }

        // En son çalışan işlemi alıyoruz
        JobExecution latestExecution = executions.get(0);

        long readCount = 0;
        long writeCount = 0;
        long skipCount = 0;

        for (StepExecution stepExecution : latestExecution.getStepExecutions()) {
            readCount += stepExecution.getReadCount();
            writeCount += stepExecution.getWriteCount();
            skipCount += stepExecution.getSkipCount();
        }

        return MigrationStatusResponse.builder()
                .jobId(jobId)
                .status(latestExecution.getStatus().name())
                .readCount(readCount)
                .writeCount(writeCount)
                .skipCount(skipCount)
                .startTime(latestExecution.getStartTime() != null ? latestExecution.getStartTime().toString() : null)
                .endTime(latestExecution.getEndTime() != null ? latestExecution.getEndTime().toString() : null)
                .exitMessage(latestExecution.getExitStatus().getExitDescription())
                .build();
    }
}