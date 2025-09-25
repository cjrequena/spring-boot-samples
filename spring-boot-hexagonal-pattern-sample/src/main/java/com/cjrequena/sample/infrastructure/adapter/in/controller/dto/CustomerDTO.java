package com.cjrequena.sample.infrastructure.adapter.in.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({
  "id",
  "name",
  "email"
})
@Schema(description = "Represents a place booking order DTO")public class CustomerDTO {
  private Long id;
  private String name;
  private String email;
  @Schema(accessMode = READ_ONLY)
  private OffsetDateTime createdAt;
  @Schema(accessMode = READ_ONLY)
  private OffsetDateTime updatedAt;
  @Schema(accessMode = READ_ONLY)
  private Long version;
}
