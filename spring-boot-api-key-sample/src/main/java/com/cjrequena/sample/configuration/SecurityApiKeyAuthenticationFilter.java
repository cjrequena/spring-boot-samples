package com.cjrequena.sample.configuration;

import com.cjrequena.sample.common.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SecurityApiKeyAuthenticationFilter implements WebFilter {
  @Value("${api.key}")
  private String _apiKey;

  @Value("${api.secret}")
  private String _apiSecret;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

    // Get the API key and secret from request headers
    String apiKey = exchange.getRequest().getHeaders().getFirst(Constants.HEADER_X_API_KEY);
    String apiSecret = exchange.getRequest().getHeaders().getFirst(Constants.HEADER_X_API_SECRET);

    // Validate the key and secret
    if (this._apiKey.equals(apiKey) && this._apiSecret.equals(apiSecret)) {
      // Continue processing the request
      return chain.filter(exchange);
    } else {
      // Reject the request and send an unauthorized error
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("Unauthorized".getBytes())));
    }
  }

}
