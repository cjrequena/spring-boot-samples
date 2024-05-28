package com.cjrequena.sample.configuration.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class PrincipalAuthToken extends AbstractAuthenticationToken {
  private final PrincipalTokenAuthUserDetails principalTokenAuthUserDetails;

  public PrincipalAuthToken(PrincipalTokenAuthUserDetails principalTokenAuthUserDetails) {
    super(principalTokenAuthUserDetails.getAuthorities());
    this.principalTokenAuthUserDetails = principalTokenAuthUserDetails;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return principalTokenAuthUserDetails;
  }
}
