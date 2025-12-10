-- Create Book table
CREATE TABLE book (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    published_year INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create index on ISBN for faster lookups
CREATE INDEX idx_book_isbn ON book(isbn);

-- Create index on author for faster searches
CREATE INDEX idx_book_author ON book(author);
