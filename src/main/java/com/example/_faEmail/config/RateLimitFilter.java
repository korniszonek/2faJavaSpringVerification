package com.example._faEmail.config;

import com.example._faEmail.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if (path.equals("/login") || path.equals("/register")) {

            String ip = req.getHeader("X-Forwarded-For");
            // if You use cloudflare, docker, nginx the normal gerRemoteAddr() will always return server ip, which will block whole site, thats why we use X-Forwarderd-For
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = req.getRemoteAddr();
            } else {
                ip = ip.split(",")[0].trim();
            }
            String key = "rl:" + path + ":" + ip;

            if (!rateLimitService.isAllowed(key, 5, 60)) {
                res.setStatus(429);
                res.getWriter().write("Too many requests");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
