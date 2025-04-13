package com.assessment.PaymentProcessor.config.security;

import com.assessment.PaymentProcessor.exception.APIException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;

@Component(value = "authFilter")
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Value("${auth.whitelisted-paths}")
    private String[] whitelistedPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Handle pre-flight OPTIONS requests
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String requestURI = request.getRequestURI();
        log.info("CURRENT PATH: {}", requestURI);

        boolean isWhitelisted = Arrays.stream(whitelistedPaths)
                .anyMatch(requestURI::startsWith);

        // If it's a whitelisted path, just proceed
        if (isWhitelisted) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = null;
        String token = null;
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = getTokenFromRequest(request);

            try {
                username = jwtService.getUserName(token); // Parse the username from the token
            } catch (ExpiredJwtException e) {
                log.error("JWT token expired: {}", e.getMessage());
                throw new APIException("JWT token has expired", HttpServletResponse.SC_UNAUTHORIZED); // Throwing custom exception
            } catch (JwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                throw new APIException("Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED); // Throwing custom exception
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validate token before proceeding
            if (jwtService.validateToken(token)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw new APIException("JWT token is not valid", HttpServletResponse.SC_UNAUTHORIZED); // Throwing custom exception
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
