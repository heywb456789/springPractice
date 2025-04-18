package com.tomato.naraclub.application.security;

import com.tomato.naraclub.application.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userKey) throws UsernameNotFoundException {
        return memberRepository.findByUserKey(userKey)
            .map(MemberUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userKey));
    }
}