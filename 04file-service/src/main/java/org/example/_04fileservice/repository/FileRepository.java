package org.example._04fileservice.repository;

import org.example._04fileservice.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileMetadata, Long> {
}