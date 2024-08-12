package com.pj.planjourney.domain.friend.dto;

import com.pj.planjourney.domain.friendrequest.entity.FriendRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestResponseDto {
    private Long senderId;
    private Long receiverId;
    private Long friendRequestId;
    private String status;
    private String senderNickname;
    private String receiverNickname;


    public FriendRequestResponseDto(FriendRequest friendRequest) {
        this.senderId = friendRequest.getSender().getId();
        this.receiverId = friendRequest.getReceiver().getId();
        this.status = friendRequest.getStatus();
        this.friendRequestId = friendRequest.getId();
        this.senderNickname = friendRequest.getSender().getNickname();
        this.receiverNickname = friendRequest.getReceiver().getNickname();
    }
}
