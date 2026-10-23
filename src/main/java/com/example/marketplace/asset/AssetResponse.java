package com.example.marketplace.asset;

import java.math.BigDecimal;
import java.time.Instant;

public record AssetResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        AssetType type,
        ModerationStatus moderationStatus,
        String fileName,
        long fileSize,
        String previewUrl,
        String downloadUrl,
        Instant createdAt) {

    static AssetResponse from(Asset a) {
        String base = "/api/assets/" + a.getId();
        return new AssetResponse(
                a.getId(), a.getTitle(), a.getDescription(), a.getPrice(), a.getType(), a.getModerationStatus(),
                a.getOriginalFileName(), a.getFileSize(),
                a.getPreviewFileName() != null ? base + "/preview" : null,
                base + "/download",
                a.getCreatedAt());
    }
}
