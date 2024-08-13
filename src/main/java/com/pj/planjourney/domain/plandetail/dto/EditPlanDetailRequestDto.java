package com.pj.planjourney.domain.plandetail.dto;

import com.pj.planjourney.domain.plandetail.entity.EditPlanType;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EditPlanDetailRequestDto {

    private EditPlanType type;    // insert, update, delete
    private Long planId;
    private Integer fromSeq;
    private Integer toSeq;
    private LocalDate fromDate;
    private LocalDate toDate;

    private String placeName;
    private Double latitude;
    private Double longitude;

    private final List<InsertRequestDto> details = new ArrayList<>();

}
