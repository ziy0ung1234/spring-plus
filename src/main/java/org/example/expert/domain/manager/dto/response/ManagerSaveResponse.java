package org.example.expert.domain.manager.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
@RequiredArgsConstructor
public class ManagerSaveResponse {

    private final Long id;
    private final UserResponse user;

    public static ManagerSaveResponse from(Manager manager) {
        return new ManagerSaveResponse(
                manager.getId(),
                UserResponse.from(manager.getUser())
        );
    }
}
