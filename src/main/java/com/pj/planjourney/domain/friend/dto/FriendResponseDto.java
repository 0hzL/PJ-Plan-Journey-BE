package com.pj.planjourney.domain.friend.dto;

import com.pj.planjourney.domain.friend.entity.Friend;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendResponseDto {
    private Long userId;
    private Long friendId;
    private String friendNickname;
    private String userNickname;

    public FriendResponseDto(Friend friend) {
        this.userId = friend.getUser().getId();
        this.friendId = friend.getFriend().getId();
        this.friendNickname = friend.getFriend().getNickname();
        this.userNickname = friend.getUser().getNickname();
    }
}
