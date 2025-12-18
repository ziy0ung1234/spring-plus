package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.expert.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_user_id")
    private User requestUser; // 등록을 한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private User target;

    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Log(User requestUser, User target, String message) {
        this.requestUser = requestUser;
        this.target = target;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public static Log managerResister(User requestUser, User target, String message) {
        return new Log(
                requestUser,
                target,
                message
        );
    }
}
