package org.example.expert.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader implements FileUploader {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public S3UploadResult upload(MultipartFile file, String dir) {

        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;
        String key = dir + "/" + fileName;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(
                    new PutObjectRequest(
                            bucket,
                            key,
                            file.getInputStream(),
                            metadata
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException("S3 upload failed", e);
        }

        String imagePath = amazonS3.getUrl(bucket, key).toString();
        return new S3UploadResult(key, imagePath);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}

