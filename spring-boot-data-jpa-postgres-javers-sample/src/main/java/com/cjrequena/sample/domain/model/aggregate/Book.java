package com.cjrequena.sample.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
  private Long id;
  private String title;
  private String author;
  private String isbn;
  private Integer publishedYear;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long version;
}
