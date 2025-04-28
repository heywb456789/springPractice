package com.tomato.naraclub.admin.auth.repository;

import com.tomato.naraclub.admin.auth.entity.AdminRefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.auth.repository
 * @fileName : AdminRefreshTokenRepository
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminRefreshTokenRepository extends JpaRepository<AdminRefreshToken, Long> {

    Optional<AdminRefreshToken> findByRefreshToken(String refreshToken);
}
