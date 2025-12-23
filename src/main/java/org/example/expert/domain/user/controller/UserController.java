package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserImageResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping
    public void changePassword(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(userId, userChangePasswordRequest);
    }

    @PostMapping(
            value = "/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserImageResponse> uploadImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(userService.uploadImage(userId, file));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void>  deleteImage(
            @AuthenticationPrincipal Long userId,
            @PathVariable long imageId
    ) {
        userService.deleteImage(userId, imageId);
        return ResponseEntity.noContent().build();
    }
}
