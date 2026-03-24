package com.pagora.migrator.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MigrationStatusResponse {
    private String jobId;
    private String status;       // STARTING, STARTED, COMPLETED, FAILED
    private long readCount;      // Kaynaktan okunan satır
    private long writeCount;     // Hedefe yazılan satır
    private long skipCount;      // Hata alıp atlanan satır
    private String startTime;
    private String endTime;
    private String exitMessage;  // Eğer hata alıp durursa hatanın detayı
}