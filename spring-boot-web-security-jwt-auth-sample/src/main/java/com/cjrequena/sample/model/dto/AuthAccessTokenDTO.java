package com.cjrequena.sample.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.Getter;

@Data
@JsonPropertyOrder(value = {
  "token_type",
  "access_token",
  "client_id",
  "scope",
  "expires_at",
  "issued_at"
})
@JsonTypeName("AuthenticationTokenDTO")
public class AuthAccessTokenDTO {
  @JsonProperty(value = "token_type")
  @Getter(onMethod = @__({@JsonProperty("token_type")}))
  public String tokenType;

  @JsonProperty(value = "access_token")
  @Getter(onMethod = @__({@JsonProperty("access_token")}))
  public String accessToken;

  @JsonProperty(value = "client_id")
  @Getter(onMethod = @__({@JsonProperty("client_id")}))
  public String clientId;

  @JsonProperty(value = "scope")
  @Getter(onMethod = @__({@JsonProperty("scope")}))
  public String scope;

  @JsonProperty(value = "expires_at")
  @Getter(onMethod = @__({@JsonProperty("expires_at")}))
  public long expiresAt;

  @JsonProperty(value = "issued_at")
  @Getter(onMethod = @__({@JsonProperty("issued_at")}))
  public long issuedAt;
}
