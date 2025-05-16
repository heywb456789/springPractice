package com.tomato.naraclub.admin.point.repository;

import com.tomato.naraclub.admin.point.repository.custom.AdminPointCustomRepository;
import com.tomato.naraclub.application.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.repository
 * @fileName : AdminPointRepository
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminPointRepository extends JpaRepository<PointHistory, Long> ,
    AdminPointCustomRepository {

}
