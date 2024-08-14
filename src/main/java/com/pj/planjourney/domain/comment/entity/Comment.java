package com.pj.planjourney.domain.comment.entity;

import com.pj.planjourney.domain.childcomment.entity.ChildComment;
import com.pj.planjourney.domain.comment.dto.CommentCreateRequestDto;
import com.pj.planjourney.domain.comment.dto.CommentUpdateRequestDto;
import com.pj.planjourney.domain.plan.dto.PlanUpdateTitleRequestDto;
import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildComment> childComments = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    public Comment(CommentCreateRequestDto commentCreateRequestDto, Plan plan, User user) {
        this.content = commentCreateRequestDto.getContent();
        this.user = user;
        this.plan = plan;
    }

    public void updateComment(CommentUpdateRequestDto requestDto) {
        if (requestDto.getContent() != null) {
            this.content = requestDto.getContent();
        }
    }
}