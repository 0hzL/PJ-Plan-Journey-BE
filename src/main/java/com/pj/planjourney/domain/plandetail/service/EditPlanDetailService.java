package com.pj.planjourney.domain.plandetail.service;

import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.plan.repository.PlanRepository;
import com.pj.planjourney.domain.plandetail.dto.EditPlanDetailRequestDto;
import com.pj.planjourney.domain.plandetail.dto.EditPlanDetailResponseDto;
import com.pj.planjourney.domain.plandetail.dto.InsertRequestDto;
import com.pj.planjourney.domain.plandetail.dto.PlanDetailDto;
import com.pj.planjourney.domain.plandetail.entity.EditPlanType;
import com.pj.planjourney.domain.plandetail.entity.PlanDetail;
import com.pj.planjourney.domain.plandetail.repository.PlanDetailRepository;
import com.pj.planjourney.global.common.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.pj.planjourney.global.common.exception.ExceptionCode.PLAN_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EditPlanDetailService {

    private final PlanRepository planRepository;
    private final PlanDetailRepository planDetailRepository;

    @Transactional
    public EditPlanDetailResponseDto editPlan(EditPlanDetailRequestDto request) {
        List<PlanDetailDto> planDetails = new ArrayList<>();
        if (request.getType() == EditPlanType.INSERT) {
            planDetails = insertPlan(request);
        } else if (request.getType() == EditPlanType.UPDATE) {
            planDetails = updatePlan(request);
        } else if (request.getType() == EditPlanType.DELETE) {
            planDetails = deletePlan(request);
        }
        return new EditPlanDetailResponseDto(planDetails, request.getPlanId());
    }

    public List<PlanDetailDto> insertPlan(EditPlanDetailRequestDto request) {
        // 해당 날짜의 모든 PlanDetail 가져오기
        Plan plan = findByPlanId(request.getPlanId());
        List<PlanDetail> planDetails = plan.getPlanDetails();

        System.out.println(request.getDetails().get(0).getPlaceName());
        for (InsertRequestDto detail : request.getDetails()) {
            // 새로운 PlanDetail 생성 및 저장
            PlanDetail newDetail = new PlanDetail(planDetails.size() + 1, plan, detail);
            planDetailRepository.save(newDetail);
            planDetails.add(newDetail);
            System.out.println(newDetail.getPlaceName());
        }

        return planDetails.stream().map(PlanDetailDto::new).toList();
    }

    public List<PlanDetailDto> deletePlan(EditPlanDetailRequestDto request) {
        // 해당 날짜의 모든 PlanDetail 가져오기
        List<PlanDetail> planDetails = planDetailRepository.findByPlanIdAndDate(request.getPlanId(), request.getFromDate());

        PlanDetail deletePlanDetail = getFromDetail(planDetails, request.getFromSeq());
        planDetailRepository.delete(deletePlanDetail);
        planDetails.remove(deletePlanDetail);

        // 삭제된 이후의 PlanDetail 순서 조정
        planDetails.stream()
                .filter(pd -> pd.getSequence() > request.getFromSeq())
                .forEach(pd -> pd.setSequence(pd.getSequence() - 1));

        return planDetails.stream().map(PlanDetailDto::new).toList();
    }


    public List<PlanDetailDto> updatePlan(EditPlanDetailRequestDto request) {
        if (request.getFromDate().equals(request.getToDate())) {  // 같은 날짜 변경
            updatePlanSameDate(request);
        } else {  // 다른 날짜 변경
            updatePlanDifferentDate(request);
        }
        List<PlanDetail> planDetails = planDetailRepository.findByPlanIdOrderByDateAscSequenceAsc(request.getPlanId());
        return planDetails.stream().map(PlanDetailDto::new).toList();
    }

    public void updatePlanSameDate(EditPlanDetailRequestDto request) {
        List<PlanDetail> planDetails = planDetailRepository.findByPlanIdAndDate(request.getPlanId(), request.getFromDate());

        PlanDetail fromDetail = getFromDetail(planDetails, request.getFromSeq());

        if (request.getFromSeq() < request.getToSeq()) {
            // 이동하는 계획이 위에서 아래로 이동하는 경우
            planDetails.stream()
                    .filter(pd -> pd.getSequence() > request.getFromSeq() && pd.getSequence() <= request.getToSeq())
                    .forEach(pd -> pd.setSequence(pd.getSequence() - 1));
        } else if (request.getFromSeq() > request.getToSeq()) {
            // 이동하는 계획이 아래에서 위로 이동하는 경우
            planDetails.stream()
                    .filter(pd -> pd.getSequence() < request.getFromSeq() && pd.getSequence() >= request.getToSeq())
                    .forEach(pd -> pd.setSequence(pd.getSequence() + 1));
        }
        fromDetail.setSequence(request.getToSeq());
    }

    public void updatePlanDifferentDate(EditPlanDetailRequestDto request) {
        // 출발 날짜에서 이동할 PlanDetail 찾기 및 순서 조정
        List<PlanDetail> fromDateDetails = planDetailRepository.findByPlanIdAndDate(request.getPlanId(), request.getFromDate());

        PlanDetail fromDetail = getFromDetail(fromDateDetails, request.getFromSeq());

        fromDateDetails.remove(fromDetail);
        fromDateDetails.stream()
                .filter(pd -> pd.getSequence() > request.getFromSeq())
                .forEach(pd -> pd.setSequence(pd.getSequence() - 1));

        // 도착 날짜로 이동할 PlanDetail 찾기 및 순서 조정
        List<PlanDetail> toDateDetails = planDetailRepository.findByPlanIdAndDate(request.getPlanId(), request.getToDate());

        toDateDetails.stream()
                .filter(pd -> pd.getSequence() >= request.getToSeq())
                .forEach(pd -> pd.setSequence(pd.getSequence() + 1));

        // 이동할 PlanDetail 의 날짜 및 순서 설정
        fromDetail.setDate(request.getToDate());
        fromDetail.setSequence(request.getToSeq());
    }

    private PlanDetail getFromDetail(List<PlanDetail> planDetails, Integer fromSeq) {
        return planDetails.stream()
                .filter(pd -> pd.getSequence().equals(fromSeq))
                .findFirst().orElseThrow();
    }

    private Plan findByPlanId(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new BusinessLogicException(PLAN_NOT_FOUND));
    }
}
