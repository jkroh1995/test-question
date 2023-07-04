package project.server.domain.comment.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import project.server.domain.cocktail.entity.Cocktail;
import project.server.domain.comment.dto.CommentDto;
import project.server.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "cocktail_id")
    private Cocktail cocktail;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "last_modified_at")
    LocalDateTime modifiedAt = LocalDateTime.now();

    public CommentDto.Response entityToResponse() {
        return CommentDto.Response.builder()
                .commentId(commentId)
                .userId(1)
                .userName("kim")
                .content(content)
                .createdAt(createdAt)
                .build();
    }
}
