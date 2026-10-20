package com.example.marketplace.asset;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record AssetUploadRequest(
        @NotBlank(message = "Укажите название") @Size(max = 120, message = "Не длиннее 120 символов") String title,
        @Size(max = 2000, message = "Не длиннее 2000 символов") String description,
        @NotNull(message = "Укажите цену") @DecimalMin(value = "0.00", message = "Цена не может быть отрицательной") BigDecimal price,
        @NotNull(message = "Выберите файл модели") MultipartFile file,
        MultipartFile preview) {
}
