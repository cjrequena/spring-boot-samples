package com.cjrequena.sample.security;

import com.cjrequena.sample.api.authentication.AuthAccessTokenAPI;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.cjrequena.sample.common.Constants.WHITELISTED_PATHS;

@Component
@RequiredArgsConstructor
public class JWTApplicationPrincipalAuthenticationFilter extends OncePerRequestFilter {

  private final JWTComponent jwtComponent;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String path = request.getServletPath();
    String authorization = request.getHeader("Authorization");

    // Check if the requested path matches any whitelisted path
    for (String whitelistedPath : WHITELISTED_PATHS) {
      if (path.startsWith(whitelistedPath.replace("/**",""))) {
        // If it matches, allow the request to proceed
        filterChain.doFilter(request, response);
      }
    }

    if (StringUtils.hasText(authorization) && path.contains(AuthAccessTokenAPI.ENDPOINT) && authorization.startsWith("Basic ")) {
      filterChain.doFilter(request, response);
    } else if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
      extractTokenFromRequest(request)
        .map(jwtComponent::decode)
        .map(jwtComponent::convertToApplicationPrincipalUserDetails)
        .map(ApplicationPrincipalAuthenticationToken::new)
        .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write("Unauthorized");
    }
  }

  private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
    var token = request.getHeader("Authorization");
    if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
      return Optional.of(token.substring(7));
    }
    return Optional.empty();
  }
}
