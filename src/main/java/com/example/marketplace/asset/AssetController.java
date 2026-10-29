package com.example.marketplace.asset;

import com.example.marketplace.storage.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService service;
    private final FileStorageService storage;

    @GetMapping
    public PagedResponse<AssetResponse> list(
            @RequestParam(required = false) AssetType type,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.list(type, pageable);
    }

    @GetMapping("/{id}")
    public AssetResponse get(@PathVariable Long id) {
        return AssetResponse.from(service.getApproved(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssetResponse> upload(@Valid @ModelAttribute AssetUploadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> preview(@PathVariable Long id) {
        Asset asset = service.getApproved(id);
        if (asset.getPreviewFileName() == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = storage.load(asset.getPreviewFileName());
        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .body(resource);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Asset asset = service.getApproved(id);
        Resource resource = storage.load(asset.getStoredFileName());
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(asset.getOriginalFileName(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(resource);
    }
}
