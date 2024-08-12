package com.pj.planjourney.domain.comment.dto;

import com.pj.planjourney.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentCreateResponseDto {
    private Long commentId;
    private String content;
    private Long userId;
    private Long planId;
    private LocalDateTime createdAt;

    public CommentCreateResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.userId = comment.getUser().getId();
        this.planId = comment.getPlan().getId();
        this.createdAt = comment.getCreatedAt();
    }
}
