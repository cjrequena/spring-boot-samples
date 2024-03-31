package com.cjrequena.sample.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTApplicationPrincipalAuthenticationFilter extends OncePerRequestFilter {

  private final JWTComponent jwtComponent;


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    extractTokenFromRequest(request)
      .map(jwtComponent::decode)
      .map(jwtComponent::convertToApplicationPrincipalUserDetails)
      .map(ApplicationPrincipalAuthenticationToken::new)
      .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));

    filterChain.doFilter(request, response); // Very important not to forget this
  }

  private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
    var token = request.getHeader("Authorization");
    if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
      return Optional.of(token.substring(7));
    }
    return Optional.empty();
  }
}
