package com.pagora.migrator.controller;

import com.pagora.migrator.dto.MigrationRequest;
import com.pagora.migrator.dto.MigrationStatusResponse;
import com.pagora.migrator.service.MigrationService;
import com.pagora.migrator.service.MigrationTrackerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/migration")
public class MigrationController {

    private final MigrationService migrationService;
    private final MigrationTrackerService trackerService;
    // Constructor injection ile ekle
    public MigrationController(MigrationService migrationService, MigrationTrackerService trackerService) {
        this.migrationService = migrationService;
        this.trackerService = trackerService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startMigration(@RequestBody MigrationRequest request) {
        // Gelen isteği loglayalım (Şimdilik)
        System.out.println("Kaynak DB: " + request.getSource().getJdbcUrl());
        System.out.println("Hedef DB: " + request.getTarget().getJdbcUrl());
        System.out.println("Tablo: " + request.getSettings().getTableName());
        // Servisi asenkron veya senkron çağır
        migrationService.startMigrationProcess(request);
        return ResponseEntity.ok("Migrasyon işlemi başlatıldı.");
    }
    @GetMapping("/status/{jobId}")
    public ResponseEntity<MigrationStatusResponse> getStatus(@PathVariable String jobId) {
        MigrationStatusResponse status = trackerService.getStatus(jobId);
        return ResponseEntity.ok(status);
    }
}