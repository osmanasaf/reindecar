package com.reindecar.service.file;

import com.reindecar.dto.file.FileResponse;
import com.reindecar.dto.file.UploadFileRequest;
import com.reindecar.entity.file.FileMetadata;
import com.reindecar.entity.file.FileReferenceType;
import com.reindecar.repository.file.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FileStorageService {

    private final FileMetadataRepository fileMetadataRepository;
    private final StorageStrategy storageStrategy;

    @Value("${file.max-size:5242880}")
    private long maxFileSize;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "mp4", "webm"
    );

    @Transactional
    public FileResponse uploadFile(MultipartFile file, UploadFileRequest request, String uploadedBy) {
        validateFile(file);

        FileMetadata metadata = FileMetadata.create(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            request.referenceType(),
            request.referenceId(),
            request.uploadType(),
            uploadedBy
        );

        String filePath = storageStrategy.store(file, metadata.getStoredName());
        metadata.setPath(filePath);

        if (request.isPublic()) {
            metadata.makePublic();
        }

        FileMetadata saved = fileMetadataRepository.save(metadata);
        log.info("File uploaded: {} -> {}", file.getOriginalFilename(), saved.getStoredName());

        return toResponse(saved);
    }

    public FileResponse getById(Long id) {
        FileMetadata metadata = fileMetadataRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("File not found"));
        return toResponse(metadata);
    }

    public List<FileResponse> getByReference(FileReferenceType type, Long referenceId) {
        return fileMetadataRepository.findByReferenceTypeAndReferenceIdOrderByUploadedAtDesc(type, referenceId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public InputStream getFileContent(Long id) {
        FileMetadata metadata = fileMetadataRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("File not found"));
        return storageStrategy.retrieve(metadata.getPath());
    }

    @Transactional
    public void deleteFile(Long id) {
        FileMetadata metadata = fileMetadataRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("File not found"));

        storageStrategy.delete(metadata.getPath());
        fileMetadataRepository.delete(metadata);
        log.info("File deleted: {}", metadata.getStoredName());
    }

    public String getPublicUrl(Long id) {
        FileMetadata metadata = fileMetadataRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("File not found"));
        return storageStrategy.getPublicUrl(metadata.getPath());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("File type not allowed");
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private FileResponse toResponse(FileMetadata m) {
        String publicUrl = storageStrategy.getPublicUrl(m.getPath());
        return new FileResponse(
            m.getId(),
            m.getFileName(),
            m.getStoredName(),
            m.getContentType(),
            m.getSize(),
            m.getExtension(),
            m.getReferenceType(),
            m.getReferenceId(),
            m.getUploadType(),
            "/api/v1/files/" + m.getId() + "/download",
            m.isPublic() ? publicUrl : "/api/v1/files/" + m.getId() + "/view",
            m.getUploadedAt()
        );
    }
}
