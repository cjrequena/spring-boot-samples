package com.cjrequena.sample.domain.model.aggregate;

import com.cjrequena.sample.domain.model.vo.EmailVO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@ToString(callSuper = true)
public class Customer {
  private final Long id;
  private final String name;
  private final EmailVO email;
  private final OffsetDateTime createdAt;
  private final OffsetDateTime updatedAt;
  private final Long version;

  @Builder
  public Customer(Long id, String name, EmailVO email, OffsetDateTime createdAt, OffsetDateTime updatedAt, Long version) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.version = version;
  }

  /**
   * Gets the email as a string for convenience.
   *
   * @return email string or null if email is not set
   */
  public String getEmailAsString() {
    return email != null ? email.email() : null;
  }

  /**
   * Checks if the customer has a valid email.
   *
   * @return true if email is present and valid
   */
  public boolean hasValidEmail() {
    return email != null && email.email() != null;
  }

}
