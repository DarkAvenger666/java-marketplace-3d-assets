package com.example.marketplace.asset;

import com.example.marketplace.moderation.ModerationRequest;
import com.example.marketplace.moderation.ModerationRequestRepository;
import com.example.marketplace.storage.FileStorageService;
import com.example.marketplace.user.User;
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

    private final AssetRepository assetRepository;
    private final FileStorageService storage;
    private final ModerationRequestRepository moderationRepository;

    @Transactional(readOnly = true)
    public PagedResponse<AssetResponse> list(Pageable pageable) {
        return PagedResponse.from(assetRepository.findAll(pageable).map(AssetResponse::from));
    }

    public PagedResponse<AssetResponse> list(AssetType type, Pageable pageable) {
        Page<Asset> page = (type == null)
                ? assetRepository.findByModerationStatus(ModerationStatus.APPROVED, pageable)
                : assetRepository.findByModerationStatusAndType(ModerationStatus.APPROVED, type, pageable);
        return PagedResponse.from(page.map(AssetResponse::from));
    }

    @Transactional(readOnly = true)
    public Asset getEntity(Long id) {
        return assetRepository.findById(id)
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
    public AssetResponse create(AssetUploadRequest req, User author) {
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
        asset.setAuthor(author);
        asset.setModerationStatus(ModerationStatus.PENDING);
        if (req.preview() != null && !req.preview().isEmpty()) {
            asset.setPreviewFileName(storage.store(req.preview(), IMAGE_FORMATS));
        }
        Asset saved = assetRepository.save(asset);
        moderationRepository.save(new ModerationRequest(saved));
        return AssetResponse.from(saved);
    }
}
