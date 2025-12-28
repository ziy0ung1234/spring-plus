package org.example.expert.domain.manager.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;

@Getter
@RequiredArgsConstructor
public class ManagerRegisterSuccessEvent {

    private final User requesUser;
    private final User targetUser;

}
