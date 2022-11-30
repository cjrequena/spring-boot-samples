package com.cjrequena.sample.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

  @Id
  private String id;

  @Field(name = "name")
  //@Indexed(name = "ttl_index", expireAfterSeconds=20)
  private String name;

  @Field(name = "description")
  private String description;

  @Field(name = "creation_date")
  private LocalDate creationDate;

}
