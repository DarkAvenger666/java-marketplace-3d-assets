package com.example.marketplace.asset;

import com.example.marketplace.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AssetService {

    private static final Set<String> MODEL_FORMATS = Set.of("glb", "gltf", "fbx", "obj", "blend", "stl", "zip");
    private static final Set<String> IMAGE_FORMATS = Set.of("png", "jpg", "jpeg", "webp");

    private final AssetRepository repository;
    private final FileStorageService storage;

    @Transactional(readOnly = true)
    public PagedResponse<AssetResponse> list(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable).map(AssetResponse::from));
    }

    public PagedResponse<AssetResponse> list(AssetType type, Pageable pageable) {
        Page<Asset> page = (type == null)
                ? repository.findByModerationStatus(ModerationStatus.APPROVED, pageable)
                : repository.findByModerationStatusAndType(ModerationStatus.APPROVED, type, pageable);
        return PagedResponse.from(page.map(AssetResponse::from));
    }

    @Transactional(readOnly = true)
    public Asset getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ассет не найден"));
    }

    @Transactional(readOnly = true)
    public Asset getApproved(Long id) {
        Asset asset = getEntity(id);
        if (asset.getModerationStatus() != ModerationStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ассет не найден");
        }
        return asset;
    }

    @Transactional
    public AssetResponse create(AssetUploadRequest req) {
        if (req.file().isEmpty()) {
            throw new IllegalArgumentException("Файл модели пустой");
        }
        Asset asset = new Asset();
        asset.setType(req.type());
        asset.setTitle(req.title().trim());
        asset.setDescription(req.description());
        asset.setPrice(req.price());
        asset.setOriginalFileName(req.file().getOriginalFilename());
        asset.setFileSize(req.file().getSize());
        asset.setStoredFileName(storage.store(req.file(), MODEL_FORMATS));
        if (req.preview() != null && !req.preview().isEmpty()) {
            asset.setPreviewFileName(storage.store(req.preview(), IMAGE_FORMATS));
        }
        return AssetResponse.from(repository.save(asset));
    }

    @Transactional
    public AssetResponse moderate(Long id, ModerationStatus status) {
        Asset asset = getEntity(id);
        asset.setModerationStatus(status);
        return AssetResponse.from(asset);
    }
}
