package com.application.amrs.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Value("${file.repo.path}")
    private String fileRepoPath;  // application.properties에서 파일 경로 가져오기

    @GetMapping("/{filename}")
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(fileRepoPath.replace("file:", "")).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 찾을 수 없거나 읽을 수 없습니다: " + filename);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예외 발생: " + e.getMessage());
        }
    }
}
