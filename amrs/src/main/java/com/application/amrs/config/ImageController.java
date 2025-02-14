package com.application.amrs.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
public class ImageController {

	@Value("${spring.web.resources.static-locations}")
	private String FILE_DIRECTORY;

    @GetMapping("/{filename}")
    public Resource getFile(@PathVariable String filename) throws Exception {
        Path filePath = Paths.get(FILE_DIRECTORY).resolve(filename).normalize();
        return new UrlResource(filePath.toUri());
    }
}
