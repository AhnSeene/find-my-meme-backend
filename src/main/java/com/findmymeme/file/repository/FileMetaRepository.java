package com.findmymeme.file.repository;

import com.findmymeme.file.domain.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileMetaRepository extends JpaRepository<FileMeta, Long> {
    Optional<FileMeta> findByFileUrl(String fileUrl);
}
