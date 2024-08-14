package com.pj.planjourney.domain.comment.repository;

import com.pj.planjourney.domain.comment.entity.Comment;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPlanId(Long planId, Pageable pageable);

}
