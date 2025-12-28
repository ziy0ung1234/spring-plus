package org.example.expert.domain.manager.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;

@Getter
@RequiredArgsConstructor
public class ManagerRegisterFailEvent {
    private final User requestUser;
    private final User targetUser;
    private final String reason;
}
