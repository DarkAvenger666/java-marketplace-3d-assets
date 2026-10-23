package com.example.marketplace.asset;

import jakarta.validation.constraints.NotNull;

public record ModerationRequest(@NotNull ModerationStatus status) {
}
