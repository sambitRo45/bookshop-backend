package com.bookshop.security;

import com.bookshop.entity.User;
import com.bookshop.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        /*
         * If Authorization header doesn't exist
         * or doesn't start with "Bearer ",
         * continue the request without authentication.
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token
        String token = authHeader.substring(7);

        try {

            // Extract email from JWT
            String email = jwtService.extractEmail(token);

            /*
             * Check whether authentication has already
             * been established for this request.
             */
            if (email != null
                    && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                // Find user from database
                User user = userRepository.findByEmail(email)
                        .orElse(null);

                if (user != null
                        && jwtService.isTokenValid(token, user)) {

                    /*
                     * Create Spring Security authentication object.
                     */
                    CustomUserDetails userDetails =
                            new CustomUserDetails(user);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * Tell Spring Security that this request
                     * has been authenticated.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception exception) {

            /*
             * Invalid or expired JWT.
             *
             * We don't throw the exception here.
             * Spring Security will decide whether the
             * requested endpoint requires authentication.
             */
            SecurityContextHolder.clearContext();
        }

        // Continue the request
        filterChain.doFilter(request, response);
    }
}