package com.tomato.naraclub.application.auth.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.tomato.naraclub.application.auth.entity.NiceAccessToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.repository
 * @fileName : NiceAccessTokenRepository
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface NiceAccessTokenRepository extends JpaRepository<NiceAccessToken, Long> {

    Optional<NiceAccessToken> findFirstByValidTrueOrderByIssuedAtDesc();
}
