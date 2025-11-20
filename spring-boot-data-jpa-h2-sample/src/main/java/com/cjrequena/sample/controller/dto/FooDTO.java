package com.cjrequena.sample.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Getter
@Setter
@Builder(toBuilder = true)        // <-- enable toBuilder()
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder(value = {
  "id",
  "name",
  "description",
  "created_at"
})
@JsonTypeName("foo")
@Schema(name = "Foo", description = "FooDTO")
@XmlRootElement
public class FooDTO {

  @JsonProperty(value = "id")
  @Getter(onMethod = @__({@JsonProperty("id")}))
  @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @NotNull(message = "name is a required field")
  @JsonProperty(value = "name", required = true)
  @Getter(onMethod = @__({@JsonProperty("name")}))
  @Schema(name = "name", requiredMode = REQUIRED)
  private String name;

  @JsonProperty(value = "description")
  @Getter(onMethod = @__({@JsonProperty("description")}))
  @Schema(name = "description")
  private String description;

  @JsonProperty(value = "created_at")
  @Getter(onMethod = @__({@JsonProperty("created_at")}))
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  @Schema(example = "yyyy-MM-dd", name = "creation_date", accessMode = Schema.AccessMode.READ_ONLY)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate createdAt;

}
