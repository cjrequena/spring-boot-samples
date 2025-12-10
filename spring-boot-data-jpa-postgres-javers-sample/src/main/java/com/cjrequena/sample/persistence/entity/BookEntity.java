package com.cjrequena.sample.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@org.javers.core.metamodel.annotation.Entity
@org.javers.core.metamodel.annotation.TypeName("Book")
@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.javers.core.metamodel.annotation.Id
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;
    
    @Column(name = "published_year")
    @org.javers.core.metamodel.annotation.PropertyName("published_year")
    private Integer publishedYear;
    
    @org.javers.core.metamodel.annotation.DiffIgnore
    @Column(name = "created_at", updatable = false)
    @org.javers.core.metamodel.annotation.PropertyName("created_at")
    private LocalDateTime createdAt;
    
    @org.javers.core.metamodel.annotation.DiffIgnore
    @Column(name = "updated_at")
    @org.javers.core.metamodel.annotation.PropertyName("updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
