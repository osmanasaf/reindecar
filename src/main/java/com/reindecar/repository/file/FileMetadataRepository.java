package com.reindecar.repository.file;

import com.reindecar.entity.file.FileMetadata;
import com.reindecar.entity.file.FileReferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    Optional<FileMetadata> findByStoredName(String storedName);

    List<FileMetadata> findByReferenceTypeAndReferenceId(FileReferenceType type, Long referenceId);

    List<FileMetadata> findByReferenceTypeAndReferenceIdOrderByUploadedAtDesc(FileReferenceType type, Long referenceId);
}
