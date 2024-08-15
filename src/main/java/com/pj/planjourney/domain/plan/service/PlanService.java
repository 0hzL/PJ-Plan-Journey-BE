package com.pj.planjourney.domain.plan.service;

import com.pj.planjourney.domain.city.entity.City;
import com.pj.planjourney.domain.city.repository.CityRepository;
import com.pj.planjourney.domain.plan.dto.*;
import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.plan.repository.PlanRepository;
import com.pj.planjourney.domain.plandetail.dto.CreatePlanDetailRequestDto;
import com.pj.planjourney.domain.plandetail.entity.PlanDetail;
import com.pj.planjourney.domain.plandetail.repository.PlanDetailRepository;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import com.pj.planjourney.domain.userPlan.entity.InvitedStatus;
import com.pj.planjourney.domain.userPlan.entity.UserPlan;
import com.pj.planjourney.domain.userPlan.repository.UserPlanRepository;
import com.pj.planjourney.global.common.exception.BusinessLogicException;
import com.pj.planjourney.global.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.pj.planjourney.global.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final PlanDetailRepository planDetailRepository;
    private final UserPlanRepository userPlanRepository;
  
    public CreatePlanResponseDto save(Long userId, CreatePlanRequestDto request) {
        User user = findUserById(userId);
        City city = findCityByName(request.getCity());

        Plan plan = planRepository.save(new Plan(request, city, user));
        for (CreatePlanDetailRequestDto detailDto : request.getPlanDetails()) {
            PlanDetail planDetail = detailDto.toEntity(plan);
            plan.addPlanDetail(planDetail);
            planDetailRepository.save(planDetail);
        }
        userPlanRepository.save(new UserPlan(user, plan, InvitedStatus.ACCEPTED));
        return new CreatePlanResponseDto(plan);
    }

    public PlanUpdateTitleResponseDto updatePlanTitle(Long planId, PlanUpdateTitleRequestDto requestDto, Long userId) {
        Plan plan = findByPlanId(planId);
        User user = findUserById(userId);

        UserPlan userPlan = userPlanRepository.findByUserAndPlan(user, plan)
                .orElseThrow(() -> new BusinessLogicException(USER_PLAN_NOT_FOUND));

        plan.updatePlan(requestDto);
        Plan updatedPlan = planRepository.save(plan);

        return new PlanUpdateTitleResponseDto(updatedPlan, userPlan);
    }

    // 상세 게시글 확인
    public PlanInfoResponseDto getPlan(Long planId) {
        Plan plan = findByPlanId(planId);

        List<PlanDetail> planDetails = planDetailRepository.findByPlanOrderBySequenceAsc(plan);
        return new PlanInfoResponseDto(plan, planDetails);
    }

    // isPublished가 true인 게시글 확인
    public Page<PlanListResponseDto> getAllPlans(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Plan> planPage = planRepository.findByIsPublishedTrue(pageable);
        return planPage.map(PlanListResponseDto::new);
    }

    // 게시글 삭제 기능
    public void deletePlan(Long planId) {
        planRepository.deleteById(planId);
    }

    // 내 일정 publish 하기
    public void publishPlan(Long planId, Long userId) {
        Plan plan = findByPlanId(planId);
        User user = findUserById(userId);
        UserPlan userPlan = userPlanRepository.findByUserAndPlan(user, plan)
                .orElseThrow(() -> new BusinessLogicException(USER_PLAN_NOT_FOUND));
        if (!userPlan.getUser().getId().equals(userId)) {
            throw new BusinessLogicException(ExceptionCode.USER_ID_MISMATCH);
        }

        if (plan.getIsPublished()) {
            plan.publish(false);
            plan.setPublishedAt();
            planRepository.save(plan);
        } else {
            plan.publish(true);
            planRepository.save(plan);
        }
    }

    // 다른 사람 일정 복사해서 가져오기
    public PlanCopyResponseDto copyPlan(Long planId, Long userId) {
        Plan originalPlan = findByPlanId(planId);
        User user = findUserById(userId);

        Plan newPlan = new Plan(originalPlan, user);

        return new PlanCopyResponseDto(planRepository.save(newPlan), user);
    }


    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(USER_NOT_FOUND));
    }
  

    private City findCityByName(String cityName) {
        return cityRepository.findByName(cityName)
                .orElseThrow(() -> new BusinessLogicException(CITY_NOT_FOUND));
    }

    private Plan findByPlanId(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new BusinessLogicException(PLAN_NOT_FOUND));
    }
}
