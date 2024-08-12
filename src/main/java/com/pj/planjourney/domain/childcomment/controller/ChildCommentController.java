package com.pj.planjourney.domain.childcomment.controller;

import com.pj.planjourney.domain.childcomment.dto.*;
import com.pj.planjourney.domain.childcomment.service.ChildCommentService;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import com.pj.planjourney.global.common.response.ApiResponse;
import com.pj.planjourney.global.common.response.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ChildCommentController {
    private final ChildCommentService childCommentService;

    @PostMapping("/{commentId}/child-comments")
    public ApiResponse<ChildCommentCreateResponseDto> createChildComment(@PathVariable Long commentId,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                         @RequestBody ChildCommentCreateRequestDto requestDto) {
        Long userId = userDetails.getUser().getId();
        ChildCommentCreateResponseDto responseDto = childCommentService.createChildComment(requestDto, commentId, userId);
        return new ApiResponse<>(responseDto, ApiResponseMessage.CHILD_COMMENT_CREATE);
    }

    @GetMapping("/child-comments/{commentId}")
    public ApiResponse<List<ChildCommentListResponseDto>> getAllChildComment(@PathVariable Long commentId) {
        List<ChildCommentListResponseDto> responseDto = childCommentService.getAllChildComment(commentId);
        return new ApiResponse<>(responseDto, ApiResponseMessage.SUCCESS);
    }

    @PatchMapping("/child-comments/{childCommentId}")
    public ApiResponse<ChildCommentUpdateResponseDto> updateChildComment(@PathVariable Long childCommentId,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                         @RequestBody ChildCommentUpdateRequestDto requestDto) {
        Long userId = userDetails.getUser().getId();
        ChildCommentUpdateResponseDto responseDto = childCommentService.updateChildComment(childCommentId, userId, requestDto);
        return new ApiResponse<>(responseDto, ApiResponseMessage.CHILD_COMMENT_UPDATE);
    }
    @DeleteMapping("/child-comments/{childCommentId}")
    public ApiResponse<Void> deleteChildComment(@PathVariable Long childCommentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        childCommentService.deleteChildComment(childCommentId, userId);
        return new ApiResponse<>(null, ApiResponseMessage.CHILD_COMMENT_DELETE);
    }
}
