package com.cjrequena.sample.shared.common.audit;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * AuditContext manages auditing metadata for a single request.
 * It is stored in a ThreadLocal and cleared automatically by the AuditAspect.
 */
@Getter
@Builder
public final class AuditContext {

  private final String author;
  private final String justification;
  private final LocalDateTime performedAt;

  // Optional: attach domain/persistence objects for auditing (e.g. updated entity)
  private final Object target;

  // Thread-bound context
  private static final ThreadLocal<AuditContext> CONTEXT =
    ThreadLocal.withInitial(() -> null);

  /** Set the context (called from controller or filter) */
  public static void set(AuditContext ctx) {
    CONTEXT.set(ctx);
  }

  /** Get the current context, may be null */
  public static AuditContext get() {
    return CONTEXT.get();
  }

  /** Get context or throw if missing (cleaner errors) */
  public static AuditContext require() {
    AuditContext ctx = CONTEXT.get();
    if (ctx == null) {
      throw new IllegalStateException("AuditContext is not initialized for this thread");
    }
    return ctx;
  }

  /** Check if context exists */
  public static boolean isPresent() {
    return CONTEXT.get() != null;
  }

  /** Clear after request (called in aspect finally) */
  public static void clear() {
    CONTEXT.remove();
  }
}
