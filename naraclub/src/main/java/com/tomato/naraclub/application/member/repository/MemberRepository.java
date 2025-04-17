package com.tomato.naraclub.application.member.repository;

import com.tomato.naraclub.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
