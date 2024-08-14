package com.pj.planjourney.domain.plan.dto;

import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.plandetail.dto.CreatePlanDetailResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
@NoArgsConstructor
public class CreatePlanResponseDto {
    private Long planId;
    private String title;
    private String city;
    private String author;
    private LocalDateTime createdAt;
    private final Map<LocalDate, List<CreatePlanDetailResponseDto>> planDetails = new TreeMap<>();

    public CreatePlanResponseDto(Plan plan) {
        planId = plan.getId();
        title = plan.getTitle();
        city = plan.getCity().getName();
        author = plan.getAuthor();
        createdAt = plan.getCreatedAt();

        List<CreatePlanDetailResponseDto> details = plan.getPlanDetails().stream().map(CreatePlanDetailResponseDto::new).toList();
        for (CreatePlanDetailResponseDto detail : details) {
            planDetails.computeIfAbsent(detail.getDate(), k -> new ArrayList<>()).add(detail);
        }
    }
}
