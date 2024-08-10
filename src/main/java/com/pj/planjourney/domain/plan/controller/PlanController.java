package com.pj.planjourney.domain.plan.controller;

import com.pj.planjourney.domain.plan.dto.*;
import com.pj.planjourney.domain.plan.service.PlanService;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import com.pj.planjourney.global.common.response.ApiResponse;
import com.pj.planjourney.global.common.response.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @Secured("ROLE_USER")
    @PostMapping
    public ApiResponse<CreatePlanResponseDto> savePlan(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestBody CreatePlanRequestDto request) {
        Long userId = userDetails.getUser().getId();
        CreatePlanResponseDto response = planService.save(userId, request);
        return new ApiResponse<>(response, ApiResponseMessage.PLAN_CREATED);
    }

    @PatchMapping("/{planId}")
    public ApiResponse<PlanUpdateTitleResponseDto> updatePlanTitle(@PathVariable Long planId,
                                                                   @RequestBody PlanUpdateTitleRequestDto requestDto,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        PlanUpdateTitleResponseDto responseDto = planService.updatePlanTitle(planId, requestDto, userId);
        return new ApiResponse<>(responseDto, ApiResponseMessage.PLAN_UPDATE_TITLE);
    }

    // PLAN 상세 보기
    @GetMapping("/{planId}")
    public ApiResponse<PlanInfoResponseDto> getPlan(@PathVariable Long planId) {
        PlanInfoResponseDto responseDto = planService.getPlan(planId);
        return new ApiResponse<>(responseDto, ApiResponseMessage.SUCCESS);
    }

    // 전체 PLAN 보기
    @GetMapping
    public ApiResponse<List<PlanListResponseDto>> getAllPlans() {
        List<PlanListResponseDto> responseDto = planService.getAllPlans();
        return new ApiResponse<>(responseDto, ApiResponseMessage.SUCCESS);
    }

    // PLAN 삭제
    @DeleteMapping("/{planId}")
    public ApiResponse<Void> deletePlan(@PathVariable Long planId) {
        planService.deletePlan(planId);
        return new ApiResponse<>(null, ApiResponseMessage.PLAN_DELETE);
    }

    // PLAN 공개하기?
    @PatchMapping("{planId}/publish")
    public ApiResponse<Void> publishPlan(@PathVariable Long planId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        planService.publishPlan(planId, userId);
        return new ApiResponse<>(null, ApiResponseMessage.PLAN_PUBLISH);
    }

    // 다른사람 PLAN을 내 일정으로 가져오기
    @PostMapping("/{planId}")
    public ApiResponse<PlanCopyResponseDto> copyPlan(@PathVariable Long planId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        PlanCopyResponseDto responseDto = planService.copyPlan(planId, userId);
        return new ApiResponse<>(responseDto, ApiResponseMessage.PLAN_COPY);
    }
}
