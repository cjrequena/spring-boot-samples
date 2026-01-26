package com.cjrequena.sample.persistence.entity;

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
@Setter
@Getter
@Entity(name = "Foo")
@Table(name = "Foo")
@NamedNativeQuery(name = "FooEntity.findByNameNamedNativeQueryExample", query = "SELECT * FROM Foo WHERE name = ?", resultClass = FooEntity.class)
@NamedQuery(name = "FooEntity.findByNameNamedQueryExample", query = "FROM Foo WHERE name = ?1")
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

  @Column(name = "created_at")
  @Convert(converter = LocalDateConverter.class)
  private LocalDate createdAt;

}
