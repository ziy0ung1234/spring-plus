package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserImageResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.entity.UserImage;
import org.example.expert.domain.user.repository.UserImageRepository;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.s3.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserImageRepository userImageRepository;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    @Transactional
    public UserImageResponse uploadImage(long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));
        userImageRepository.findByUserAndIsMainTrue(user)
                .ifPresent(UserImage::disableMain);

        // S3 업로드
        S3UploadResult result = s3Uploader.upload(
                file,
                "users/" + userId + "/profile"
        );

        // 새 이미지 저장
        UserImage newImage = new UserImage(
                user,
                result.getImagePath(),
                result.getImageKey(),
                true
        );

        userImageRepository.save(newImage);

        return new UserImageResponse(result.getImagePath());
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    @Transactional
    public void deleteImage(long userId, long imageId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        UserImage image = userImageRepository.findById(imageId)
                .orElseThrow(() -> new InvalidRequestException("Image not found"));

        if (!image.getUser().getId().equals(user.getId())) {
            throw new InvalidRequestException("권한이 없습니다.");
        }

        if (image.isMain()) {
            image.disableMain();
        }

        userImageRepository.delete(image);
    }

}
/*
    이미지 여러개 등록- 메인, 히스토리
    이미지 삭제 users/{usersId}/image
    특정 이미지 삭제 users/{usersId}/image/{imageId}
    메인 이미지가 삭제되면? -> 그냥 비어있음
    S3에서는 삭제 진행 ->  업로드시 s3에 먼저 업로드 후 db저장

 */
