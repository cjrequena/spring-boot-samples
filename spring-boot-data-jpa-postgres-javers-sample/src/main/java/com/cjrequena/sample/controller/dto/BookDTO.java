package com.cjrequena.sample.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @NotBlank(message = "Title is required")
    @JsonProperty("title")
    private String title;
    
    @NotBlank(message = "Author is required")
    @JsonProperty("author")
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @JsonProperty("isbn")
    private String isbn;
    
    @NotNull(message = "Published year is required")
    @JsonProperty("published_year")
    private Integer publishedYear;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
