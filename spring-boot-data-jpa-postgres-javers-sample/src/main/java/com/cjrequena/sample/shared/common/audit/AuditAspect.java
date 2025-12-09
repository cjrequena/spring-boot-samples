package com.cjrequena.sample.shared.common.audit;

import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.aggregate.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

  private final Javers javers;
  private final BookMapper bookMapper;

  @Around("@annotation(auditable)")
  public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {

    AuditContext context = AuditContext.get();

    if (context == null) {
      log.warn("No audit context found for method: {}", joinPoint.getSignature().getName());
      return joinPoint.proceed(); // skip auditing
    }

    String action = auditable.action().isEmpty()
      ? joinPoint.getSignature().getName()
      : auditable.action();

    Object result;
    try {
      result = joinPoint.proceed();

      // ----------------------------
      // Determine what to audit
      // ----------------------------
      Object auditedObject = resolveAuditableObject(result, joinPoint.getArgs());

      if (auditedObject == null) {
        log.debug("No auditable object found for method: {}", joinPoint.getSignature().getName());
        return result; // nothing to commit
      }

      // Convert to entity if needed
      Object entity = convertToEntityIfNeeded(auditedObject);

      // ----------------------------
      // Commit to JaVers
      // ----------------------------
      Map<String, String> commitProperties = new HashMap<>();
      commitProperties.put("action", action);
      commitProperties.put("justification", context.getJustification());
      commitProperties.put("performed_at", context.getPerformedAt().toString());

      javers.commit(context.getAuthor(), entity, commitProperties);

      log.info("Audit committed - EntityType: {}, Action: {}, Author: {}", entity.getClass().getSimpleName(), action, context.getAuthor());

    } finally {
      AuditContext.clear();
    }

    return result;
  }

  /**
   * Find the object to audit.
   * 1. Prefer return value if it is a BookAggregate
   * 2. Otherwise scan method arguments
   */
  private Object resolveAuditableObject(Object result, Object[] args) {
    // Case 1: result is a BookAggregate
    if (result instanceof Book) {
      return result;
    }

    // Case 2: find BookAggregate in method arguments
    for (Object arg : args) {
      if (arg instanceof Book) {
        return arg;
      }
    }

    // Nothing found → skip audit
    return null;
  }

  /**
   * Convert domain aggregate to persistence entity (mapper used only for known types)
   */
  private Object convertToEntityIfNeeded(Object obj) {
    if (obj instanceof Book bookAgg) {
      return bookMapper.toEntity(bookAgg);
    }
    return obj; // already an entity or other object
  }
}
