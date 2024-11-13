package com.cjrequena.sample.configuration.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserAuthToken extends AbstractAuthenticationToken {
  private final AuthUserDetails authUserDetails;

  public UserAuthToken(AuthUserDetails authUserDetails) {
    super(authUserDetails.getAuthorities());
    this.authUserDetails = authUserDetails;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return authUserDetails;
  }
}
