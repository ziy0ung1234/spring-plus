package org.example.expert.domain.comment.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;

@Getter
@RequiredArgsConstructor
public class CommentSaveResponse {

    private final Long id;
    private final String contents;
    private final UserResponse user;

    public static CommentSaveResponse from(Comment comment) {
        return new CommentSaveResponse(
                comment.getId(),
                comment.getContents(),
                UserResponse.from(comment.getUser())
        );
    }
}
