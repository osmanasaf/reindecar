package com.reindecar.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
@ConditionalOnProperty(name = "file.storage.type", havingValue = "s3")
@Slf4j
public class S3StorageStrategy implements StorageStrategy {

    @Value("${aws.s3.bucket:reindecar-files}")
    private String bucketName;

    @Value("${aws.s3.region:eu-central-1}")
    private String region;

    @Value("${aws.cloudfront.url:}")
    private String cloudfrontUrl;

    @Override
    public String store(MultipartFile file, String storedName) {
        log.info("S3 Storage: Would upload {} to bucket {}", storedName, bucketName);
        return "s3://" + bucketName + "/" + storedName;
    }

    @Override
    public InputStream retrieve(String path) {
        log.info("S3 Storage: Would retrieve {}", path);
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public void delete(String path) {
        log.info("S3 Storage: Would delete {}", path);
    }

    @Override
    public String getPublicUrl(String path) {
        if (cloudfrontUrl != null && !cloudfrontUrl.isEmpty()) {
            String fileName = path.replace("s3://" + bucketName + "/", "");
            return cloudfrontUrl + "/" + fileName;
        }
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + 
               path.replace("s3://" + bucketName + "/", "");
    }
}
