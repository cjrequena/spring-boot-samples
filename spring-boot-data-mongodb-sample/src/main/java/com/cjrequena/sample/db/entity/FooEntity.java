package com.cjrequena.sample.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Document(collection = "foo")
@Setter
@Getter
public class FooEntity {


  private String id;

  @NotNull
  private String name;

  private String description;

  private LocalDate creationDate;

}
