package com.cjrequena.sample.domain.model.vo;

import com.cjrequena.sample.domain.exception.service.InvalidEmailServiceException;

import java.util.regex.Pattern;

public record EmailVO(String email) {
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  public EmailVO {
    if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
      throw new InvalidEmailServiceException("Invalid email format: " + email);
    }
  }
}
