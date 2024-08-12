package com.pj.planjourney.domain.friend.service;

import com.pj.planjourney.domain.friend.dto.*;
import com.pj.planjourney.domain.friend.entity.Friend;
import com.pj.planjourney.domain.friend.repository.FriendRepository;
import com.pj.planjourney.domain.friendrequest.entity.FriendRequest;
import com.pj.planjourney.domain.friendrequest.repository.FriendRequestRepository;
import com.pj.planjourney.domain.notification.service.NotificationService;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import com.pj.planjourney.global.common.exception.BusinessLogicException;
import com.pj.planjourney.global.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final NotificationService notificationService;

    @Transactional
    public FriendRequestSendResponseDto sendFriendRequest(FriendRequestSendDto requestCreateDto, Long userId) {
        User sender = getUserById(userId);
        User receiver = userRepository.findByEmail(requestCreateDto.getReceiverEmail())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        if (friendRequestRepository.findBySenderAndReceiver(sender, receiver) != null) {
            throw new BusinessLogicException(ExceptionCode.REQUEST_ALREADY_SENT);
        }

        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);
        notificationService.sendFriendRequestNotification(receiver.getId(), sender.getId());

        return new FriendRequestSendResponseDto(savedFriendRequest, sender);
    }


    public List<FriendRequestResponseDto> getSentFriendRequests(Long userId) {
        User user = getUserById(userId);
        return friendRequestRepository.findBySender(user).stream()
                .map(FriendRequestResponseDto::new).toList();
    }

    public List<FriendRequestResponseDto> getReceivedFriendRequests(Long userId) {
        User user = getUserById(userId);
        return friendRequestRepository.findByReceiver(user).stream()
                .map(FriendRequestResponseDto::new).toList();
    }

    @Transactional
    public void acceptFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REQUEST_NOT_FOUND));

        friendRequest.accept();

        User sender = friendRequest.getSender();
        User receiver = friendRequest.getReceiver();

        Friend friend1 = new Friend(sender, receiver);
        Friend friend2 = new Friend(receiver, sender);

        friendRepository.save(friend1);
        friendRepository.save(friend2);

        notificationService.sendFriendAcceptedNotification(receiver.getId(), sender.getId());

        friendRequestRepository.delete(friendRequest);
    }

    @Transactional
    public void rejectFriendRequest(Long senderId) {
        FriendRequest friendRequest = friendRequestRepository.findById(senderId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REQUEST_NOT_FOUND));

        friendRequest.reject();

        friendRequestRepository.delete(friendRequest);
    }

    public List<FriendResponseDto> getFriends(Long userId) {
        User user = getUserById(userId);
        return friendRepository.findByUser(user).stream()
                .map(FriendResponseDto::new).toList();
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        Friend friendship1 = friendRepository.findByUserAndFriend(user, friend);
        Friend friendship2 = friendRepository.findByUserAndFriend(friend, user);

        if (friendship1 != null) {
            friendRepository.delete(friendship1);
        }

        if (friendship2 != null) {
            friendRepository.delete(friendship2);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

}
