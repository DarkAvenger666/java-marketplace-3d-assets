package com.example.marketplace.moderation;

import com.example.marketplace.asset.Asset;
import com.example.marketplace.asset.ModerationStatus;
import com.example.marketplace.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "moderation_requests")
@Getter
@Setter
public class ModerationRequest {
    protected ModerationRequest() {}
    public ModerationRequest(Asset asset) { this.asset = asset; }

    @Version
    @Setter(AccessLevel.NONE)
    private Long version;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)           // пока заявку не взяли, null
    private User moderator;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private ModerationStatus status = ModerationStatus.PENDING;

    @Column(length = 2000)
    private String moderatorComment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant resolvedAt;
}
