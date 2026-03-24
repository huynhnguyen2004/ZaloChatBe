package com.huynh.ZaloCloneBe.config;

import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {


    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(new MACVerifier(jwtProperties.getSecret()))) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
            Date expiry = jwt.getJWTClaimsSet().getExpirationTime();

            if (expiry.before(new Date())) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
            Long userId = Long.parseLong(jwt.getJWTClaimsSet().getSubject());
            String role=jwt.getJWTClaimsSet().getStringClaim("role");
            String jti=jwt.getJWTClaimsSet().getJWTID();
            String key="blacklist:"+jti;


            if (userId==null) {
                throw new AppException(ErrorCode.UNAUTHORIZED);

            }
            if(redisTemplate.hasKey(key)){
                throw new AppException(ErrorCode.TOKEN_INVALID);


            }



            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        filterChain.doFilter(request, response);
    }
}
