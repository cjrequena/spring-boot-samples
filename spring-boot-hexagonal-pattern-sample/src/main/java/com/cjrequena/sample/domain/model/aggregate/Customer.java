package com.cjrequena.sample.domain.model.aggregate;

import com.cjrequena.sample.domain.model.vo.EmailVO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@ToString(callSuper = true)
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Customer {
  private Long id;
  private String name;
  private EmailVO email;

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
