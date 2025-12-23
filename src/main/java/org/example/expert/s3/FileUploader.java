package org.example.expert.s3;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    S3UploadResult upload(MultipartFile file, String dir);
}

