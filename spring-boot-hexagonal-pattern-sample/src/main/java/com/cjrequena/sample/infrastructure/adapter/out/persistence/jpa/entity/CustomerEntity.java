package com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "customer")
public class CustomerEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    //@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, updatable = false, insertable = false)
    //@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;
}
