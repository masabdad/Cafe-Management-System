package com.cafe.management.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    private String userName = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Allow login, forgotPassword, and signup to pass through without authentication
        if (request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader("Authorization");
            String token = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                userName = jwtUtil.extractUserName(token);
            }

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customerUserDetailsService.loadUserByUsername(userName);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    public Boolean isAdmin() {
        return "admin".equalsIgnoreCase((String) getClaims().get("role"));
    }

    public Boolean isUser() {
        return "user".equalsIgnoreCase((String) getClaims().get("role"));
    }

    public String getCurrentUser() {
        return userName;
    }

    public boolean isEmployee() {
        return "employee".equalsIgnoreCase((String) getClaims().get("role"));
    }

    private Claims getClaims() {
        return jwtUtil.extractAllClaims(userName);  // Use this method to extract claims
    }

    public String getRole() {
        String token = getAuthorizationHeader();
        if (token != null) {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey("MasabDad786")
                        .parseClaimsJws(token)
                        .getBody();
                return claims.get("role", String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getAuthorizationHeader() {
        return "Bearer " + jwtUtil.extractUserName(userName);  // Implement this method as per your JWT utility
    }
}
