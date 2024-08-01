package com.pj.planjourney.domain.friend.dto;

import com.pj.planjourney.domain.friendrequest.entity.FriendRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestResponseDto {
    private Long senderId;
    private Long receiverId;
    private String status;


    public FriendRequestResponseDto(FriendRequest friendRequest) {
        this.senderId = friendRequest.getSender().getId();
        this.receiverId = friendRequest.getReceiver().getId();
        this.status = friendRequest.getStatus();
    }
}
