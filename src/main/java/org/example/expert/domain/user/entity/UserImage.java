package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@Getter
@Entity
@NoArgsConstructor
@Table(name="user_images")
public class UserImage extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String imagePath;

    @Column
    private String imageKey;

    @Column
    private boolean isMain;

    public UserImage(User user, String imagePath, String imageKey, boolean isMain) {
        this.user = user;
        this.imagePath = imagePath;
        this.imageKey = imageKey;
        this.isMain = isMain;
    }


    public void disableMain() {
        this.isMain = false;
    }
}
