package com.ailadeekshith.schoolManagement.config;

import com.ailadeekshith.schoolManagement.model.StudentUser;
import com.ailadeekshith.schoolManagement.repository.StudentUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class StudentJwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final StudentUserRepository studentUserRepo;

    public StudentJwtAuthFilter(JwtUtil jwtUtil, StudentUserRepository studentUserRepo) {
        this.jwtUtil = jwtUtil;
        this.studentUserRepo = studentUserRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                studentUserRepo.findByUsername(username).ifPresent(studentUser -> {
                    if (studentUser.getStatus() == StudentUser.Status.ACTIVE
                            && SecurityContextHolder.getContext().getAuthentication() == null) {
                        var auth = new UsernamePasswordAuthenticationToken(
                                studentUser, null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
                        );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                });
            }
        }
        chain.doFilter(req, res);
    }
}
