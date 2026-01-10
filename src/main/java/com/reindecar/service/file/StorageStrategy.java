package com.reindecar.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageStrategy {

    String store(MultipartFile file, String storedName);

    InputStream retrieve(String path);

    void delete(String path);

    String getPublicUrl(String path);
}
