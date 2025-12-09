package com.cjrequena.sample.domain.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Foo {
  private Long id;
  private String name;
  private String description;
  private LocalDate createdAt;
}
