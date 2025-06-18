package com.tomato.naraclub.application.auth.repository;

import com.tomato.naraclub.application.auth.entity.NiceCryptoRequest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.repository
 * @fileName : NiceCryptoRepository
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface NiceCryptoRepository extends JpaRepository<NiceCryptoRequest, Long> {

    Optional<NiceCryptoRequest> findByTokenVersionId(String tokenVersionId);
}
