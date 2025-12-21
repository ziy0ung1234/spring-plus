package org.example.expert.domain.user.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSearchResponse {
    private final Long id;
    private final String email;
    private final String nickname;

}
