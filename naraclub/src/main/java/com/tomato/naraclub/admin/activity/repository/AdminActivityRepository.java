package com.tomato.naraclub.admin.activity.repository;

import com.tomato.naraclub.admin.activity.repository.custom.AdminActivityCustomRepository;
import com.tomato.naraclub.application.member.code.ActivityReviewStage;
import com.tomato.naraclub.application.member.entity.MemberActivity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.activity.repository
 * @fileName : AdminActivityRepository
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminActivityRepository extends JpaRepository<MemberActivity, Long>,
    AdminActivityCustomRepository {

    Optional<MemberActivity> findByIdAndStage(Long id, ActivityReviewStage activityReviewStage);
}
