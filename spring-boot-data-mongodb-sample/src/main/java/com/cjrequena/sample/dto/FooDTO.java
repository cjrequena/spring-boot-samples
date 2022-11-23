package com.cjrequena.sample.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Data
@JsonPropertyOrder(value = {
  "id",
  "name",
  "description",
  "creation_date"
})
@JsonTypeName("foo")
@Schema(name = "Foo", description = "FooDTO")
@XmlRootElement
public class FooDTO {

  @JsonProperty(value = "id")
  @Getter(onMethod = @__({@JsonProperty("id")}))
  @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY)
  private String id;

  @NotNull(message = "name is a required field")
  @JsonProperty(value = "name", required = true)
  @Getter(onMethod = @__({@JsonProperty("name")}))
  @Schema(name = "name", required = true)
  private String name;

  @JsonProperty(value = "description")
  @Getter(onMethod = @__({@JsonProperty("description")}))
  @Schema(name = "description")
  private String description;

  @JsonProperty(value = "creation_date")
  @Getter(onMethod = @__({@JsonProperty("creation_date")}))
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  @Schema(example = "yyyy-MM-dd", name = "creation_date", accessMode = Schema.AccessMode.READ_ONLY)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate creationDate;

}
