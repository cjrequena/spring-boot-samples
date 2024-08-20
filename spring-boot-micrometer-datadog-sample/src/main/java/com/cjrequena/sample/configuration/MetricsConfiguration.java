package com.cjrequena.sample.configuration;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.regex.Pattern;

@Configuration
public class MetricsConfiguration {

  @Value("${management.metrics.server.excluded-uris:}")
  private List<String> excludedUris;

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  @Bean
  public CountedAspect countedAspect(MeterRegistry meterRegistry) {
    return new CountedAspect(meterRegistry);
  }

  @Bean
  @SuppressWarnings("java:S3740") // The raw type is necessary
  public MeterRegistryCustomizer meterRegistryCustomizer() {
    return registry ->
      registry
        .config()
        .meterFilter(
          MeterFilter.deny(
            id -> "http.server.requests".equals(id.getName()) && excludedUris.stream().map(Pattern::compile).anyMatch(pattern -> pattern.matcher(id.getTag("uri")).matches())
          )
        );
  }

}
