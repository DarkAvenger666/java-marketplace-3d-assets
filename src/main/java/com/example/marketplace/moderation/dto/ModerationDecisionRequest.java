package com.example.marketplace.moderation.dto;

import com.example.marketplace.asset.ModerationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModerationDecisionRequest(
        @NotNull(message = "Укажите решение") ModerationStatus status,
        @Size(max = 2000, message = "Не длиннее 2000 символов") String comment) {}
