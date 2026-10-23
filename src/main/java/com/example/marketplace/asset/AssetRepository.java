package com.example.marketplace.asset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Page<Asset> findByModerationStatus(ModerationStatus status, Pageable pageable);
    Page<Asset> findByModerationStatusAndType(ModerationStatus status, AssetType type, Pageable pageable);
}
