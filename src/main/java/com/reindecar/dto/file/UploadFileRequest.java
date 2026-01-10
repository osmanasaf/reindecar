package com.reindecar.dto.file;

import com.reindecar.entity.file.FileReferenceType;
import com.reindecar.entity.file.FileUploadType;
import jakarta.validation.constraints.NotNull;

public record UploadFileRequest(
    @NotNull(message = "Reference type is required")
    FileReferenceType referenceType,

    Long referenceId,

    @NotNull(message = "Upload type is required")
    FileUploadType uploadType,

    boolean isPublic
) {}
