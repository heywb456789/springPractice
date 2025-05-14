package com.tomato.naraclub.application.point.repository;

import com.tomato.naraclub.application.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.point.repository
 * @fileName : PointRepository
 * @date : 2025-05-14
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface PointRepository extends JpaRepository<PointHistory, Long> {

}
