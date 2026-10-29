package com.example.marketplace.moderation;

import com.example.marketplace.asset.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationRequestRepository extends JpaRepository<ModerationRequest, Long> {
    @EntityGraph(attributePaths = {"asset", "asset.author", "moderator"})
    Page<ModerationRequest> findByStatus(ModerationStatus status, Pageable pageable);
}
