package com.cjrequena.sample.service;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FooService {

  private final Counter sayHello1RequestsCounter;
  private final Timer sayHello1ResponseTimer;
  private final AtomicInteger activeSayHello1Requests;

  @Autowired
  private DataDogEventService dataDogEventService;

  public FooService(MeterRegistry registry) {
    sayHello1RequestsCounter = Counter.builder("say_hello1.requests.counted")
      .description("Number of say_hello1 requested")
      .register(registry);

    sayHello1ResponseTimer = Timer.builder("say_hello.response.timed")
      .description("Time taken to process a say_hello1")
      .register(registry);

    activeSayHello1Requests = new AtomicInteger(0);
    Gauge.builder("say_hello1.active.requests", activeSayHello1Requests::get)
      .description("Active say_hello1 requests")
      .register(registry);
  }

  @Counted(value = "say_hello.requests.counted")
  @Timed(value = "say_hello.response.timed")
  public String sayHello() {
    // Pause execution for 3 seconds (3000 milliseconds)
    try {
      // Pause execution for 3 seconds (3000 milliseconds)
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      // Handle the exception if the sleep is interrupted
      e.printStackTrace();
    }
    return "Hello Fooes";
  }

  public String sayHello1() {
    this.activeSayHello1Requests.incrementAndGet();
    try {
      sayHello1ResponseTimer.record(() -> {
        try {
          Thread.sleep(1000); // Simulating delay
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

      });
    } finally {
      sayHello1RequestsCounter.increment();
      activeSayHello1Requests.decrementAndGet();
      // Send warning
      //this.dataDogEventService.warningEvent("warning.event.foo", "creating a foo warning.", Event.Priority.NORMAL,"warning.event.foo.tag");
      return "Hello Fooes";
    }
  }
}
