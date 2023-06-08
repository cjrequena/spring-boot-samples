package com.cjrequena.sample.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateConverter;

import java.time.LocalDate;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Entity
@Table(name = "foo")
@Setter
@Getter
public class FooEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "creation_date")
  @Convert(converter = LocalDateConverter.class)
  private LocalDate creationDate;

}
