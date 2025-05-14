package com.tomato.naraclub.application.member.repository;

import com.tomato.naraclub.application.member.entity.TwitterAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwitterAccountRepository extends JpaRepository<TwitterAccount, Long> {
}
