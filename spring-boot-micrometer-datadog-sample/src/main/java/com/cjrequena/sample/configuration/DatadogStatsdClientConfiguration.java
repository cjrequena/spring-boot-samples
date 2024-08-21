package com.cjrequena.sample.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("management.statsd.metrics.export")
@Data
public class DatadogStatsdClientConfiguration {

  private boolean enabled;
  public String host;
  public Integer port;
  public String protocol;
  public String flavor;


}
