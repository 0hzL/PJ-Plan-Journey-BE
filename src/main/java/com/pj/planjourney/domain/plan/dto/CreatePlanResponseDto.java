package com.pj.planjourney.domain.plan.dto;

import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.plandetail.dto.CreatePlanDetailResponseDto;
import com.pj.planjourney.domain.plandetail.dto.PlanDetailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@NoArgsConstructor
public class CreatePlanResponseDto {
    private Long planId;
    private String title;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private final Map<LocalDate, List<CreatePlanDetailResponseDto>> planDetails = new TreeMap<>();

    public CreatePlanResponseDto(Plan plan) {
        planId = plan.getId();
        title = plan.getTitle();
        city = plan.getCity().getName();
        startDate = plan.getStartDate();
        endDate = plan.getEndDate();
        createdAt = plan.getCreatedAt();

        List<CreatePlanDetailResponseDto> details = plan.getPlanDetails().stream().map(CreatePlanDetailResponseDto::new).toList();
        for (CreatePlanDetailResponseDto detail : details) {
            planDetails.computeIfAbsent(detail.getDate(), k -> new ArrayList<>()).add(detail);
        }
    }
}
