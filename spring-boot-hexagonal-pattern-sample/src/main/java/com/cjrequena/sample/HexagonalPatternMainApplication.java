package com.cjrequena.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
  "com.cjrequena.sample.adapter",
  "com.cjrequena.sample.configuration",
  "com.cjrequena.sample.application"
})
public class HexagonalPatternMainApplication {
  public static void main(String[] args) {
    SpringApplication.run(HexagonalPatternMainApplication.class, args);
  }
}
