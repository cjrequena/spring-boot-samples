package com.cjrequena.sample;

import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableCaching
public class MainApplication implements CommandLineRunner {

  public static void main(String[] args){
    SpringApplication.run(MainApplication.class, args);
  }

  @Override
  @SneakyThrows
  public void run(String... args){}
}
