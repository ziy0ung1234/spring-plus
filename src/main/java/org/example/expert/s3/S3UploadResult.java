package org.example.expert.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3UploadResult {
    private String imageKey;
    private String imagePath;
}
