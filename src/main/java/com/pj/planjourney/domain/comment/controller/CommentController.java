package com.pj.planjourney.domain.comment.controller;

import com.pj.planjourney.domain.comment.dto.*;
import com.pj.planjourney.domain.comment.service.CommentService;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import com.pj.planjourney.global.common.response.ApiResponse;
import com.pj.planjourney.global.common.response.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plans/{planId}")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ApiResponse<CommentCreateResponseDto> createComment(@PathVariable Long planId,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                               @RequestBody CommentCreateRequestDto requestDto) {
        Long userId = userDetails.getUser().getId();
        CommentCreateResponseDto responseDto = commentService.createComment(requestDto, userId, planId);
        return new ApiResponse<>(responseDto, ApiResponseMessage.COMMENT_CREATE);
    }

    @GetMapping("/comments")
    public ApiResponse<Page<CommentListResponseDto>> getAllComment(@PathVariable Long planId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Page<CommentListResponseDto> responseDto = commentService.getAllComment(planId, page, size);
        return new ApiResponse<>(responseDto, ApiResponseMessage.SUCCESS);
    }

    @PatchMapping("/comments/{commentId}")
    public ApiResponse<CommentUpdateResponseDto> updateComment(@PathVariable Long planId,
                                                               @PathVariable Long commentId,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                               @RequestBody CommentUpdateRequestDto requestDto) {
        Long userId = userDetails.getUser().getId();
        CommentUpdateResponseDto responseDto = commentService.updateComment(planId, commentId, userId, requestDto);
        return new ApiResponse<>(responseDto, ApiResponseMessage.COMMENT_UPDATE);
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long planId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        commentService.deleteComment(planId, commentId, userId);
        return new ApiResponse<>(null, ApiResponseMessage.COMMENT_DELETE);
    }

}