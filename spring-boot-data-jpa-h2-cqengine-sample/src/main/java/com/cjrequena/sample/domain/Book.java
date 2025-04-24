package com.cjrequena.sample.domain;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private String isbn;
    private String title;
    private String author;

    // CQEngine Attributes
    public static final Attribute<Book, String> ISBN = new SimpleAttribute<>("isbn") {
        public String getValue(Book book, QueryOptions queryOptions) {
            return book.getIsbn();
        }
    };

    public static final Attribute<Book, String> TITLE = new SimpleAttribute<>("title") {
        @Override
        public String getValue(Book book, QueryOptions queryOptions) {
            return book.getTitle();
        }
    };

    public static final Attribute<Book, String> AUTHOR = new SimpleAttribute<>("author") {
        public String getValue(Book book, QueryOptions queryOptions) {
            return book.getAuthor();
        }
    };
}
