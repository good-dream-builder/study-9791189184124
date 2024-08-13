package com.songko.study9791189184124.member.security.filter;

import com.songko.study9791189184124.member.security.auth.CustomUserPrincipal;
import com.songko.study9791189184124.member.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Component
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    /**
     * JWTCheckFilter가 동작하지 않아야 하는 경로를 지정
     *
     * @param request
     * @return
     * @throws ServletException
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (request.getServletPath().startsWith("/api/v1/token/")) {
            return true;
        }

        return false;
    }

    /**
     * Access Token을 꺼내서 검증해서 문제가 없는 경우에는 컨트롤러 혹은 다음 필터들이 동작하도록 구성
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("JWTCheckFilter doFilterInternal.....................");
        log.info("Request URI : {}", request.getRequestURI());

        String headerStr = request.getHeader("Authorization");
        log.info("headerStr: {}", headerStr);

        // Access Token이 없는 경우
        if (headerStr == null || !headerStr.startsWith("Bearer ")) {
            handleException(response, new Exception("ACCESS TOKEN NOT FOUND"));
            return;
        }

        String accessToken = headerStr.substring(7);    // 7번째 인덱스 부터 끝까지.('Bearer ' 떼기)

        try {
            Map<String, Object> tokenMap = jwtUtil.validateToken(accessToken);
            log.info("tokenMap: {}", tokenMap);

            String mid = tokenMap.get("mid").toString();
            // 권한이 여러 개인 경우에는 콤마(,)로 구분해서 처리
            String[] roles = tokenMap.get("role").toString().split(",");

            // 토큰 검증 결과를 이용해서 Authentication 객체를 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    new CustomUserPrincipal(mid),   // Principal
                    null,   // Credentials
                    Arrays.stream(roles)    // Authorities
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList())
            );

            //SecurityContextHolder에 Authentication 객체를 저장.
            // 이후에 SecurityContextHolder를 이용해서 Authentication 객체를 꺼내서 사용할 수 있다.
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter()
                .println("{\"error\": \"" + e.getMessage() + "\"}");
    }
}
