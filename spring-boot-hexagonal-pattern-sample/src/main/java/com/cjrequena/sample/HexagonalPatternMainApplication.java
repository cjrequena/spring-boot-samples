package com.cjrequena.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"infrastructure", "config"})
public class HexagonalPatternMainApplication {
  public static void main(String[] args) {
    SpringApplication.run(HexagonalPatternMainApplication.class, args);
  }
}
