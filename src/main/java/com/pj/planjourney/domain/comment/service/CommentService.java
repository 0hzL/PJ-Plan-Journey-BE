package com.pj.planjourney.domain.comment.service;

import com.pj.planjourney.domain.comment.dto.*;
import com.pj.planjourney.domain.comment.entity.Comment;
import com.pj.planjourney.domain.comment.repository.CommentRepository;
import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.plan.repository.PlanRepository;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import com.pj.planjourney.global.common.exception.BusinessLogicException;
import com.pj.planjourney.global.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    public CommentCreateResponseDto createComment(CommentCreateRequestDto requestDto, Long userId, Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PLAN_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        Comment comment = new Comment(requestDto, plan, user);

        Comment savedComment = commentRepository.save(comment);
        return new CommentCreateResponseDto(savedComment);
    }

    public Page<CommentListResponseDto> getAllComment(Long planId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByPlanId(planId, pageable);
        return comments.map(CommentListResponseDto::new);
    }

    public CommentUpdateResponseDto updateComment(Long planId, Long commentId, Long userId, CommentUpdateRequestDto requestDto) {
        Comment comment = getCommentById(commentId);

        if (!comment.getPlan().getId().equals(planId)) {
            throw new BusinessLogicException(ExceptionCode.PLAN_ID_MISMATCH);
        }
        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessLogicException(ExceptionCode.USER_ID_MISMATCH);
        }
        comment.updateComment(requestDto);
        Comment updatedComment = commentRepository.save(comment);
        return new CommentUpdateResponseDto(updatedComment);
    }

    public void deleteComment(Long planId, Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);

        if (!comment.getPlan().getId().equals(planId)) {
            throw new BusinessLogicException(ExceptionCode.PLAN_ID_MISMATCH);
        }
        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessLogicException(ExceptionCode.USER_ID_MISMATCH);
        }
        commentRepository.delete(comment);

    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }
}