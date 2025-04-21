package com.tomato.naraclub.application.security;

import com.tomato.naraclub.application.security.MemberUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final MemberUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
        JwtTokenProvider tokenProvider,
        MemberUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                boolean valid = tokenProvider.validateToken(token);
                if (!valid) {
                    // 검증 메서드가 false만 반환하는 경우까지 401 처리
                    throw new BadCredentialsException("Invalid or expired token");
                }
                // valid == true 면 정상 토큰
                Long memberId = Long.valueOf(tokenProvider.getSubject(token));
                UserDetails user = userDetailsService.loadUserByUsername(memberId.toString());
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (ExpiredJwtException ex) {
                // 토큰 만료 시
                throw new BadCredentialsException("Token expired");
            } catch (JwtException ex) {
                // 그 외 서명 불일치 등
                throw new BadCredentialsException("Invalid token");
            }catch (BadCredentialsException ex) {
                throw new BadCredentialsException("Invalid token");
            }
        }
        filterChain.doFilter(request, response);
    }


}
