package com.cjrequena.sample.shared.common.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditContext {
  private String author;
  private String justification;
  private LocalDateTime performedAt;

  private static final ThreadLocal<AuditContext> CONTEXT = new ThreadLocal<>();

  public static void set(AuditContext context) {
    CONTEXT.set(context);
  }

  public static AuditContext get() {
    return CONTEXT.get();
  }

  public static void clear() {
    CONTEXT.remove();
  }
}
