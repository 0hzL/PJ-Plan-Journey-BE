package com.pj.planjourney.domain.friend.controller;

import com.pj.planjourney.domain.friend.dto.*;
import com.pj.planjourney.domain.friend.service.FriendService;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import com.pj.planjourney.global.common.response.ApiResponse;
import com.pj.planjourney.global.common.response.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/request")
    public ApiResponse<FriendRequestSendResponseDto> sendFriendRequest(@RequestBody FriendRequestSendDto requestCreateDto,
                                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        FriendRequestSendResponseDto friendRequestSendResponseDto = friendService.sendFriendRequest(requestCreateDto, userId);
        return new ApiResponse<>(friendRequestSendResponseDto, ApiResponseMessage.REQUEST_SENT);
    }

    @GetMapping("/sentLists")
    public ApiResponse<List<FriendRequestResponseDto>> getSentFriendRequests(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        List<FriendRequestResponseDto> requests = friendService.getSentFriendRequests(userId);
        return new ApiResponse<>(requests, ApiResponseMessage.REQUEST_RETRIEVED);
    }

    @GetMapping("/receivedLists")
    public ApiResponse<List<FriendRequestResponseDto>> getReceivedFriendRequests(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        List<FriendRequestResponseDto> requests = friendService.getReceivedFriendRequests(userId);
        return new ApiResponse<>(requests, ApiResponseMessage.RECEIVED_RETRIEVED);
    }

    @PostMapping("/accept/{friendRequestId}")
    public ApiResponse<Void> acceptFriendRequest(@PathVariable Long friendRequestId) {
        friendService.acceptFriendRequest(friendRequestId);
        return new ApiResponse<>(null, ApiResponseMessage.REQUEST_ACCEPTED);
    }

    @PostMapping("/reject/{friendRequestId}")
    public ApiResponse<Void> rejectFriendRequest(@PathVariable Long friendRequestId) {
        friendService.rejectFriendRequest(friendRequestId);
        return new ApiResponse<>(null, ApiResponseMessage.REQUEST_REJECTED);
    }

    @GetMapping
    public ApiResponse<List<FriendResponseDto>> getFriends(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        List<FriendResponseDto> friends = friendService.getFriends(userId);
        return new ApiResponse<>(friends, ApiResponseMessage.FRIENDS_RETRIEVED);
    }

    @DeleteMapping
    public ApiResponse<Void> deleteFriend(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestBody FriendDeleteDto friendDeleteDto) {
        Long userId = userDetails.getUser().getId();
        friendService.deleteFriend(userId, friendDeleteDto);
        return new ApiResponse<>(null, ApiResponseMessage.FRIEND_DELETED);
    }
}
