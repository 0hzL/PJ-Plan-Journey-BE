package com.pj.planjourney.domain.notification.service;

import com.pj.planjourney.domain.notification.dto.NotificationListsDto;
import com.pj.planjourney.domain.notification.dto.NotificationMessage;
import com.pj.planjourney.domain.notification.entity.Notification;
import com.pj.planjourney.domain.notification.repository.NotificationRepository;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import com.pj.planjourney.global.common.exception.BusinessLogicException;
import com.pj.planjourney.global.common.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 친구 요청 알림 생성
    @Transactional
    public void sendFriendRequestNotification(Long recipientId, Long senderId) {
        User sender = findBySenderId(senderId);
        String message = sender.getNickname() + "님이 친구 요청을 보냈습니다.";
        createNotification(message, "FRIEND_REQUEST", recipientId);

        messagingTemplate.convertAndSendToUser(
                recipientId.toString(),  // 수신자의 유저 ID를 사용
                "/queue/notifications",  // 목적지 경로 설정
                new NotificationMessage(message)
        );
    }

    // 친구 요청 수락 알림 생성
    @Transactional
    public void sendFriendAcceptedNotification(Long recipientId, Long senderId) {
        User sender = findBySenderId(senderId);
        String message = sender.getNickname() + "님이 친구 요청을 수락했습니다.";
        createNotification(message, "FRIEND_ACCEPTED", recipientId);

        messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/notifications",
                new NotificationMessage(message)
        );
    }
    // 친구 요청 거절 알림 생성
    @Transactional
    public void sendFriendRejectedNotification(Long recipientId, Long senderId) {
        User sender = findBySenderId(senderId);
        String message = sender.getNickname() + "님이 친구 요청을 거절했습니다.";
        createNotification(message, "FRIEND_REJECTED", recipientId);
    }

    // 여행 하루 전 리마인드 알림 생성
    @Transactional
    public void sendTravelReminderNotification(Long userId, String message) {
        createNotification(message, "TRAVEL_REMINDER", userId);
    }

    // 친구 초대 알림 생성
    @Transactional
    public void sendFriendInviteNotification(Long recipientId, Long senderId) {
        User sender = findBySenderId(senderId);
        String message = sender.getNickname() + "님이 여행에 초대했습니다.";
        createNotification(message, "FRIEND_INVITE", recipientId);
    }

    // 모든 알림 가져오기
    public List<NotificationListsDto> getAllNotifications(Long userId) {
        User user = findBySenderId(userId);
        return notificationRepository.findByUser(user)
                .stream()
                .map(NotificationListsDto::new).toList();
    }

    // 읽지 않은 알림 목록 가져오기
    public List<NotificationListsDto> getUnreadNotifications(Long userId) {
        User user = findBySenderId(userId);
        return notificationRepository.findByUserAndIsReadFalse(user)
                .stream()
                .map(NotificationListsDto::new).toList();
    }

    // 알림 읽음 상태로 업데이트
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.NOTIFICATION_NOT_FOUND));
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    // 알림 생성
    @Transactional
    public void createNotification(String message, String noticeType, Long userId) {
        User user = findBySenderId(userId);
        Notification notification = new Notification(message, noticeType, user);
        notificationRepository.save(notification);
    }

    private User findBySenderId(Long senderId) {
        return userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.SENDER_NOT_FOUND));
    }
}
