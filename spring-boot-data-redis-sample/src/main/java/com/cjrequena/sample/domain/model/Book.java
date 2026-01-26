package com.cjrequena.sample.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book implements Serializable {
    private String isbn;
    private String title;
    private String author;
}
