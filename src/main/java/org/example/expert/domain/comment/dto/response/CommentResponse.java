package org.example.expert.domain.comment.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
@RequiredArgsConstructor
public class CommentResponse {

    private final Long id;
    private final String contents;
    private final UserResponse user;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContents(),
                UserResponse.from(comment.getUser())
        );
    }
}
