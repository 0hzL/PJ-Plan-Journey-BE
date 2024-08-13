package com.pj.planjourney.domain.userPlan.service;

import com.pj.planjourney.domain.friend.entity.Friend;
import com.pj.planjourney.domain.friend.repository.FriendRepository;
import com.pj.planjourney.domain.notification.service.NotificationService;
import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.plan.repository.PlanRepository;
import com.pj.planjourney.domain.sse.service.EventService;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import com.pj.planjourney.domain.userPlan.dto.AcceptInvitePlanRequestDto;
import com.pj.planjourney.domain.userPlan.dto.InviteFriendsRequestDto;
import com.pj.planjourney.domain.userPlan.dto.InvitedPlanResponseDto;
import com.pj.planjourney.domain.userPlan.entity.InvitedStatus;
import com.pj.planjourney.domain.userPlan.entity.UserPlan;
import com.pj.planjourney.domain.userPlan.repository.UserPlanRepository;
import com.pj.planjourney.global.common.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pj.planjourney.global.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
public class UserPlanService {

    private final UserPlanRepository userPlanRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final NotificationService notificationService;

    /**
     * 해당 일정으로 친구를 초대하는 메소드
     *
     * @param planId  초대할 일정 id
     * @param request 초대할 친구들 id 정보가 담겨있는 dto
     */
    @Transactional
    public void invite(User user, Long planId, InviteFriendsRequestDto request) {
        Plan plan = planRepository.findById(planId).orElseThrow();

        if (request.getInviteFriends().isEmpty())
            throw new BusinessLogicException(FRIEND_NOT_INVITED);

        for (Long friendId : request.getInviteFriends()) {
            User friend = findUserById(friendId);
            isFriend(user, friend);

            UserPlan userPlan = new UserPlan(friend, plan, InvitedStatus.PENDING);
            userPlanRepository.save(userPlan);

            notificationService.sendFriendInviteNotification(friendId ,user.getId());
        }
    }

    /**
     * 초대받은 일정 목록 조회하는 메소드
     * 해당 id, plan id 와 title 반환.
     * 추후 필요한 정보로 변경 예정.
     *
     * @param userId 본인 id
     * @return 초대받은 일정 목록
     */
    public List<InvitedPlanResponseDto> getInvitedPlans(Long userId) {
        List<UserPlan> plans = userPlanRepository.findByUserIdAndInvitedStatus(userId, InvitedStatus.PENDING);
        return plans.stream().map(this::toInvitedPlanResponseDto).toList();
    }

    private InvitedPlanResponseDto toInvitedPlanResponseDto(UserPlan userPlan) {
        return new InvitedPlanResponseDto(userPlan);
    }

    /**
     * 일정 편집 초대를 수락하는 메소드
     *
     * @param userPlanId 초대받은 일정 정보 id
     * @return 해당 일정 id 반환
     */
    @Transactional
    public AcceptInvitePlanRequestDto acceptInvitePlan(Long userPlanId) {
        UserPlan userPlan = findUserPlanById(userPlanId);
        isPending(userPlan);

        userPlan.toAccept();
        return new AcceptInvitePlanRequestDto(userPlan);
    }

    /**
     * 알정 편집 초대를 거절하는 메소드
     *
     * @param userPlanId 초대받은 일정 정보 id
     */
    @Transactional
    public void rejectInvitePlan(Long userPlanId) {
        UserPlan userPlan = findUserPlanById(userPlanId);
        isPending(userPlan);

        userPlan.toReject();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(USER_NOT_FOUND));
    }

    private void isFriend(User user, User friend) {
        Friend findFriend = friendRepository.findByUserAndFriend(user, friend);
        if (findFriend == null) throw new BusinessLogicException(FRIEND_NOT_FOUND);
    }

    private UserPlan findUserPlanById(Long userPlanId) {
        return userPlanRepository.findById(userPlanId)
                .orElseThrow(() -> new BusinessLogicException(REQUEST_NOT_FOUND));
    }

    private void isPending(UserPlan userPlan) {
        if (userPlan.getInvitedStatus() != InvitedStatus.PENDING)
            throw new BusinessLogicException(RESPONSE_ALREADY);
    }

}
