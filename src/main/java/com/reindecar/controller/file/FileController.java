package com.reindecar.controller.file;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.file.FileResponse;
import com.reindecar.dto.file.UploadFileRequest;
import com.reindecar.entity.file.FileReferenceType;
import com.reindecar.service.file.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "File upload and management endpoints")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload file", description = "Uploads a file")
    public ApiResponse<FileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("referenceType") FileReferenceType referenceType,
            @RequestParam(value = "referenceId", required = false) Long referenceId,
            @RequestParam("uploadType") String uploadType,
            @RequestParam(value = "isPublic", defaultValue = "false") boolean isPublic,
            Authentication authentication) {

        UploadFileRequest request = new UploadFileRequest(
            referenceType,
            referenceId,
            com.reindecar.entity.file.FileUploadType.valueOf(uploadType),
            isPublic
        );

        String uploadedBy = authentication != null ? authentication.getName() : "anonymous";
        FileResponse response = fileStorageService.uploadFile(file, request, uploadedBy);
        return ApiResponse.success("File uploaded successfully", response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get file info", description = "Returns file metadata")
    public ApiResponse<FileResponse> getFileInfo(@PathVariable Long id) {
        FileResponse file = fileStorageService.getById(id);
        return ApiResponse.success(file);
    }

    @GetMapping("/references/{type}/{id}")
    @Operation(summary = "Get files by reference", description = "Returns files for a reference")
    public ApiResponse<List<FileResponse>> getByReference(
            @PathVariable FileReferenceType type, 
            @PathVariable Long id) {
        List<FileResponse> files = fileStorageService.getByReference(type, id);
        return ApiResponse.success(files);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download file", description = "Downloads file content")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) throws IOException {
        FileResponse fileInfo = fileStorageService.getById(id);
        InputStream inputStream = fileStorageService.getFileContent(id);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.fileName() + "\"")
            .contentType(MediaType.parseMediaType(fileInfo.contentType()))
            .body(new InputStreamResource(inputStream));
    }

    @GetMapping("/{id}/view")
    @Operation(summary = "View file", description = "Views file inline")
    public ResponseEntity<InputStreamResource> viewFile(@PathVariable Long id) throws IOException {
        FileResponse fileInfo = fileStorageService.getById(id);
        InputStream inputStream = fileStorageService.getFileContent(id);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileInfo.fileName() + "\"")
            .contentType(MediaType.parseMediaType(fileInfo.contentType()))
            .body(new InputStreamResource(inputStream));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete file", description = "Deletes a file")
    public ApiResponse<Void> deleteFile(@PathVariable Long id) {
        fileStorageService.deleteFile(id);
        return ApiResponse.success("File deleted successfully", null);
    }
}
