package com.reindecar.entity.file;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "file_metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileMetadata extends BaseEntity {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String fileName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String storedName;

    @Size(max = 100)
    @Column(length = 100)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Size(max = 10)
    @Column(length = 10)
    private String extension;

    @Size(max = 500)
    @Column(length = 500)
    private String path;

    @Size(max = 64)
    @Column(length = 64)
    private String checksum;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FileReferenceType referenceType;

    @Column
    private Long referenceId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FileUploadType uploadType;

    @Size(max = 100)
    @Column(length = 100)
    private String uploadedBy;

    @Column(nullable = false)
    private Instant uploadedAt;

    @Column(nullable = false)
    private boolean isPublic = false;

    public static FileMetadata create(
            String fileName,
            String contentType,
            Long size,
            FileReferenceType referenceType,
            Long referenceId,
            FileUploadType uploadType,
            String uploadedBy) {

        FileMetadata file = new FileMetadata();
        file.fileName = fileName;
        file.storedName = generateStoredName(fileName);
        file.contentType = contentType;
        file.size = size;
        file.extension = extractExtension(fileName);
        file.referenceType = referenceType;
        file.referenceId = referenceId;
        file.uploadType = uploadType;
        file.uploadedBy = uploadedBy;
        file.uploadedAt = Instant.now();
        file.isPublic = false;
        return file;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public void makePublic() {
        this.isPublic = true;
    }

    public void makePrivate() {
        this.isPublic = false;
    }

    private static String generateStoredName(String originalName) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExtension(originalName);
        return ext != null ? uuid + "." + ext : uuid;
    }

    private static String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
