package com.cjrequena.sample.service;

import com.cjrequena.sample.configuration.DatadogStatsdClientConfiguration;
import com.timgroup.statsd.Event;
import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DataDogEventService {

  private StatsDClient statsDClient;
  private Clock clock;

  private final DatadogStatsdClientConfiguration datadogStatsdClientConfiguration;

  @PostConstruct
  public void start() {
    this.clock = Clock.systemDefaultZone();

    if (statsDClient != null || !datadogStatsdClientConfiguration.isEnabled()) {
      return;
    }

    String host = datadogStatsdClientConfiguration.getHost();
    Integer port = datadogStatsdClientConfiguration.getPort();

    if (host == null || host.isEmpty() || port == null) {
      log.error("Datadog disabled: host and port are mandatory");
      return;
    }

    statsDClient = new NonBlockingStatsDClientBuilder()
      .hostname(host)
      .port(port)
      .prefix(null) // No prefix.
      .constantTags(null) // No predefined tags.
      .build();
  }

  public void recordEvent(Event event, String... tags) {
    statsDClient.recordEvent(event, tags);
  }

  public void warningEvent(String title, String text, Event.Priority priority, String... tags) {
    Event event = Event.builder()
      .withTitle(title)
      .withText(text)
      .withDate(clock.millis())
      .withAlertType(Event.AlertType.WARNING)
      .withPriority(priority)
      .build();
    statsDClient.recordEvent(event, tags);
  }

  public void errorEvent(String title, String text, Event.Priority priority, String... tags) {
    Event event = Event.builder()
      .withTitle(title)
      .withText(text)
      .withDate(clock.millis())
      .withAlertType(Event.AlertType.ERROR)
      .withPriority(priority)
      .build();
    statsDClient.recordEvent(event, tags);
  }

  public void infoEvent(String title, String text, String... tags) {
    Event event = Event.builder()
      .withTitle(title)
      .withText(text)
      .withDate(clock.millis())
      .withAlertType(Event.AlertType.INFO)
      .build();
    statsDClient.recordEvent(event, tags);
  }

  public void successEvent(String title, String text, String... tags) {
    Event event = Event.builder()
      .withTitle(title)
      .withText(text)
      .withDate(clock.millis())
      .withAlertType(Event.AlertType.SUCCESS)
      .build();
    statsDClient.recordEvent(event, tags);
  }
}
