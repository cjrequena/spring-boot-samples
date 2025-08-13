package com.cjrequena.sample.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
  "com.cjrequena.sample"
})
public class HexagonalPatternMainApplication {
  public static void main(String[] args) {
    SpringApplication.run(HexagonalPatternMainApplication.class, args);
  }
}
