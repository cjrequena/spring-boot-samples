package com.cjrequena.sample.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class ApplicationPrincipalAuthenticationToken extends AbstractAuthenticationToken {
  private final ApplicationPrincipalUserDetails applicationPrincipalUserDetails;

  public ApplicationPrincipalAuthenticationToken(ApplicationPrincipalUserDetails applicationPrincipalUserDetails) {
    super(applicationPrincipalUserDetails.getAuthorities());
    this.applicationPrincipalUserDetails = applicationPrincipalUserDetails;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return applicationPrincipalUserDetails;
  }
}
