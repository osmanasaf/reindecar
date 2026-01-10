package com.reindecar.dto.file;

import com.reindecar.entity.file.FileReferenceType;
import com.reindecar.entity.file.FileUploadType;

import java.time.Instant;

public record FileResponse(
    Long id,
    String fileName,
    String storedName,
    String contentType,
    Long size,
    String extension,
    FileReferenceType referenceType,
    Long referenceId,
    FileUploadType uploadType,
    String downloadUrl,
    String viewUrl,
    Instant uploadedAt
) {}
