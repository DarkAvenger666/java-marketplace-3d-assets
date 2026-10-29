package com.example.marketplace.moderation;

import com.example.marketplace.asset.ModerationStatus;
import com.example.marketplace.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ModerationRequestService {
    private final ModerationRequestRepository repository;

    @Transactional
    public void resolve(Long requestId, User moderator, ModerationStatus decision, String comment) {
        if (decision == ModerationStatus.PENDING) throw new IllegalArgumentException("Нужно решение");
        ModerationRequest r = repository.findById(requestId).orElseThrow(/* 404 */);
        if (r.getStatus() != ModerationStatus.PENDING) throw new IllegalStateException("Заявка уже обработана");

        r.setStatus(decision);
        r.setModerator(moderator);
        r.setModeratorComment(comment);
        r.setResolvedAt(Instant.now());
        r.getAsset().setModerationStatus(decision);
    }
}
