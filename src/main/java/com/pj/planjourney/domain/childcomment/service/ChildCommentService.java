package com.pj.planjourney.domain.childcomment.service;

import com.pj.planjourney.domain.childcomment.dto.*;
import com.pj.planjourney.domain.childcomment.entity.ChildComment;
import com.pj.planjourney.domain.childcomment.repository.ChildCommentRepository;
import com.pj.planjourney.domain.comment.entity.Comment;
import com.pj.planjourney.domain.comment.repository.CommentRepository;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChildCommentService {
    private final ChildCommentRepository childCommentRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public ChildCommentCreateResponseDto createChildComment(ChildCommentCreateRequestDto requestDto,Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ChildComment childComment = new ChildComment(requestDto, comment, user);

        ChildComment savedChildComment = childCommentRepository.save(childComment);
        return new ChildCommentCreateResponseDto(savedChildComment);
    }


    public List<ChildCommentListResponseDto> getAllChildComment(Long childCommentId) {
        List<ChildComment> childComments = childCommentRepository.findByCommentId(childCommentId);

        return childComments.stream()
                .map(ChildCommentListResponseDto::new)
                .toList();
    }
    public ChildCommentUpdateResponseDto updateChildComment(Long childCommentId, Long userId, ChildCommentUpdateRequestDto requestDto) {
        ChildComment childComment = getChildCommentById(childCommentId);

        if(!childComment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User ID mismatch");
        }
        childComment.updateChildComment(requestDto);
        ChildComment updatedChildComment = childCommentRepository.save(childComment);
        return new ChildCommentUpdateResponseDto(updatedChildComment);
    }

    private ChildComment getChildCommentById(Long childCommentId) {
        return childCommentRepository.findById(childCommentId)
                .orElseThrow(() -> new IllegalArgumentException("ChildComment not found"));
    }

    public void deleteChildComment(Long childCommentId, Long userId) {
        ChildComment childComment = getChildCommentById(childCommentId);

        if(!childComment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User ID mismatch");
        }
        childCommentRepository.delete(childComment);
    }
}