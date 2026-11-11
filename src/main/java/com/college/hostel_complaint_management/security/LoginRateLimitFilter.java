package com.college.hostel_complaint_management.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 2;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        boolean isLoginAttempt = request.getMethod().equals("POST")
                && request.getRequestURI().equals("/login");

        if (isLoginAttempt) {

            String ip = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(ip, key -> createBucket());

            if (!bucket.tryConsume(1)) {
                response.sendRedirect(
                        request.getContextPath() + "/login?limitExceeded"
                );
                return;
            }
        }

        filterChain.doFilter(request, response); // everything else continues normally
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(MAX_ATTEMPTS)
                .refillGreedy(MAX_ATTEMPTS, REFILL_PERIOD)
                .build();

        return Bucket.builder().addLimit(limit).build();
    }
}