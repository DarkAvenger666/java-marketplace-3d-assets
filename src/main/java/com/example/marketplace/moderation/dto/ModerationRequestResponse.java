package com.example.marketplace.moderation.dto;

import com.example.marketplace.asset.Asset;
import com.example.marketplace.asset.AssetType;
import com.example.marketplace.asset.ModerationStatus;
import com.example.marketplace.moderation.ModerationRequest;

import java.time.Instant;

public record ModerationRequestResponse(
        Long id, Long assetId, String assetTitle, String authorName, AssetType assetType,
        String fileName, long fileSize, ModerationStatus status,
        String moderatorName, String moderatorComment, Instant createdAt, Instant resolvedAt) {

    static ModerationRequestResponse from(ModerationRequest r) {
        Asset a = r.getAsset();
        return new ModerationRequestResponse(
                r.getId(), a.getId(), a.getTitle(), a.getAuthor().getName(), a.getType(),
                a.getOriginalFileName(), a.getFileSize(), r.getStatus(),
                r.getModerator() != null ? r.getModerator().getName() : null,
                r.getModeratorComment(), r.getCreatedAt(), r.getResolvedAt());
    }
}
