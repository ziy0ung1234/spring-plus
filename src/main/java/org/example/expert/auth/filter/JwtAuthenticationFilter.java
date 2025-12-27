package org.example.expert.auth.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 *  PrincipalDetailService - username/password -> DB에서 사용자 정보를 불러오기 위한 책임
 *  굳이 UserDetail 구현체가 필요 없는 이유
 *  - JWT Filter에서 직접 검증
 *  - claim에 userid 있음 -> Controller에서 userID만 알면 됨
 *  -
 * */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String bearerToken = request.getHeader(JwtConstant.AUTHORIZATION.getValue());

        if (bearerToken == null || !bearerToken.startsWith(JwtConstant.BEARER.getValue())) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = jwtUtil.substringToken(bearerToken);
            Claims claims = jwtUtil.extractClaims(token);

            Long userId = Long.parseLong(claims.getSubject()); // JWT의 subject를 사용자 식별자
            String role = claims.get("userRole", String.class);

            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority(JwtConstant.ROLE.getValue() + role));

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,        // principal
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
        filterChain.doFilter(request, response);
    }
}
