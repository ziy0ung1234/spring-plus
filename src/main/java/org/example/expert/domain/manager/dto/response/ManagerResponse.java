package org.example.expert.domain.manager.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
@RequiredArgsConstructor
public class ManagerResponse {

    private final Long id;
    private final UserResponse user;

    public static ManagerResponse from(Manager manager) {
        return new ManagerResponse(
                manager.getId(),
                new UserResponse(
                        manager.getUser().getId(),
                        manager.getUser().getEmail()
                )
        );
    }
}
