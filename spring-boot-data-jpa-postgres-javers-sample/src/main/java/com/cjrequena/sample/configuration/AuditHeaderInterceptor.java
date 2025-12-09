package com.cjrequena.sample.configuration;

import com.cjrequena.sample.shared.common.util.AuditContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
@Slf4j
public class AuditHeaderInterceptor implements HandlerInterceptor {
  private static final String HEADER_ACTION = "X-Action";
  private static final String HEADER_AUTHOR = "X-Author";
  private static final String HEADER_JUSTIFICATION = "X-Justification";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String action = request.getHeader(HEADER_ACTION);
    String author = request.getHeader(HEADER_AUTHOR);
    String justification = request.getHeader(HEADER_JUSTIFICATION);

    if (action != null || author != null || justification != null) {
      AuditContext context = AuditContext
        .builder()
        .action(action)
        .author(author != null ? author : "anonymous")
        .performedAt(LocalDateTime.now())
        .justification(justification)
        .build();

      AuditContext.set(context);
      log.debug("Audit context set from headers: {}", context);
    }

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    AuditContext.clear();
  }
}
