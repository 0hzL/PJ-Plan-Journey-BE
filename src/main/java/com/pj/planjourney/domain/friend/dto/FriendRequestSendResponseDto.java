package com.pj.planjourney.domain.friend.dto;

import com.pj.planjourney.domain.friendrequest.entity.FriendRequest;
import com.pj.planjourney.domain.user.entity.User;
import lombok.Getter;

@Getter
public class FriendRequestSendResponseDto {
    private Long friendRequestId;
    private String senderNickname;

    public FriendRequestSendResponseDto(FriendRequest savedFriendRequest, User sender) {
        this.friendRequestId = savedFriendRequest.getId();
        this.senderNickname = sender.getNickname();
    }
}
