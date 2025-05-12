package com.tomato.naraclub.common.security;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.security.AdminUserDetailsService;
import com.tomato.naraclub.admin.user.code.AdminRole;
import com.tomato.naraclub.admin.user.entity.Admin;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.security.MemberUserDetailsService;
import com.tomato.naraclub.common.exception.UnAuthorizationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ADMIN_AUTH_LOGOUT = "/admin/auth/logout";
    private final JwtTokenProvider tokenProvider;
    private final MemberUserDetailsService memberDetailsService;
    private final AdminUserDetailsService adminDetailsService;

    public JwtAuthenticationFilter(
        JwtTokenProvider tokenProvider,
        MemberUserDetailsService memberDetailsService,
        AdminUserDetailsService adminDetailsService
    ) {
        this.tokenProvider = tokenProvider;
        this.memberDetailsService = memberDetailsService;
        this.adminDetailsService = adminDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain)
        throws ServletException, IOException {

        String uri = req.getRequestURI();
        // /admin/auth/login 과 리소스는 예외
        boolean isAdminPage = uri.startsWith("/admin")
            && !uri.startsWith("/admin/auth/");
        String token = resolveToken(req);

        // 1) 관리자 페이지인데 토큰이 없는 경우 → 바로 로그인 페이지로
        if (isAdminPage && token == null) {
            res.sendRedirect(ADMIN_AUTH_LOGOUT);
            return;
        }

        boolean refreshed = false;
        String newAccessToken = null;

        try {
            if (token != null) {
                try {
                    // 기존 액세스 토큰 검증
                    validateAndAuthenticate(token);
                } catch (ExpiredJwtException ex) {
                    // 만료된 경우 기존 Refresh 로직
                    String refresh = resolveRefreshToken(req);
                    if (refresh != null && tokenProvider.validateToken(refresh)) {
                        String role = tokenProvider.getClaims(refresh).get("role").toString();
                        UserDetails user = loadUserByRole(role, refresh);

                        newAccessToken = issueNewAccessToken(user, role);
                        String newRefresh = issueNewRefreshToken(user, role);

                        addCookie(res, "ACCESS_TOKEN", newAccessToken, 60 * 60);
                        addCookie(res, "REFRESH_TOKEN", newRefresh, 7 * 24 * 3600);

                        UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null,
                                user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        refreshed = true;
                    } else {
                        throw new BadCredentialsException("리프레시 토큰이 유효하지 않습니다.");
                    }
                } catch (UnAuthorizationException uex) {
                    res.sendRedirect(ADMIN_AUTH_LOGOUT);
                    return;
                }
            }
        } catch (BadCredentialsException | JwtException ex) {
            // 2) 관리자 페이지인데 토큰 검증/재발급 실패 시 → 로그인 페이지로
            if (isAdminPage) {
                res.sendRedirect(ADMIN_AUTH_LOGOUT);
                return;
            }
            // API 에선 그냥 401 처리로 넘기기
            SecurityContextHolder.clearContext();
        }

        // 3) 새로 발급된 토큰이 있다면 헤더에도 추가
        if (refreshed && newAccessToken != null) {
            res.setHeader("Authorization", "Bearer " + newAccessToken);
        }

        chain.doFilter(req, res);
    }

    /**
     * 헤더 우선, 없으면 쿠키에서 ACCESS_TOKEN 꺼내기
     */
    private String resolveToken(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String t = h.substring(7).trim();
            return t.isEmpty() ? null : t;
        }
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("ACCESS_TOKEN".equals(c.getName())) {
                    String v = c.getValue();
                    if (v != null && !v.trim().isEmpty()) {
                        return v.trim();
                    }
                }
            }
        }
        return null;
    }


    /**
     * 쿠키에서 REFRESH_TOKEN 꺼내기
     */
    private String resolveRefreshToken(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("REFRESH_TOKEN".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 토큰 파싱 시에는 tokenProvider.getKey() 를 사용
     */
    private void validateAndAuthenticate(String token) {
        Jwts.parserBuilder()
            .setSigningKey(tokenProvider.getKey())
            .build()
            .parseClaimsJws(token);

        String role = tokenProvider.getClaims(token).get("role").toString();
        UserDetails user = loadUserByRole(role, token);

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * 역할에 따라 UserDetailsService 선택
     */
    private UserDetails loadUserByRole(String role, String token) {
        try {
            AdminRole adminRole = AdminRole.of(role);
            String username = tokenProvider.getSubject(token);
            return adminDetailsService.loadUserByUsername(username);
        } catch (IllegalArgumentException e) {
            // enum 에 없으면 일반회원
            Long memberId = Long.valueOf(tokenProvider.getSubject(token));
            return memberDetailsService.loadUserByUsername(memberId.toString());
        }
    }

    private String issueNewAccessToken(UserDetails user, String role) {
        if (user instanceof AdminUserDetails) {
            Admin admin = ((AdminUserDetails) user).getAdmin();
            return tokenProvider.createAccessTokenForAdmin(admin);
        } else {
            Member member = ((MemberUserDetails) user).getMember();
            return tokenProvider.createAccessToken(member);
        }
    }

    private String issueNewRefreshToken(UserDetails user, String role) {
        if (user instanceof AdminUserDetails) {
            Admin admin = ((AdminUserDetails) user).getAdmin();
            return tokenProvider.createRefreshTokenForAdmin(admin, false);
        } else {
            Member member = ((MemberUserDetails) user).getMember();
            return tokenProvider.createRefreshToken(member, false);
        }
    }


    private void addCookie(HttpServletResponse res, String name, String value, int maxAgeSec) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(Duration.ofSeconds(maxAgeSec))
            .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}


