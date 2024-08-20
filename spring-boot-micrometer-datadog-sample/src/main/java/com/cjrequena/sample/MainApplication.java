package com.cjrequena.sample;

import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication implements CommandLineRunner {

  public static void main(String... args) {
    SpringApplication.run(MainApplication.class, args);
  }

  @Override
  @SneakyThrows
  public void run(String... args) throws Exception {
  }
}
