package com.pj.planjourney.domain.plandetail.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class InsertRequestDto {
    private LocalDate date;
    private String placeName;
    private Double latitude;
    private Double longitude;
}