package com.cjrequena.sample.service;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FooService {


  @Counted(value = "retrieve.counted")
  @Timed(value = "retrieve.response-time")
  public String retrieve() {
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
}
