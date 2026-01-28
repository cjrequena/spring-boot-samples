# BookController - Autocomplete & Search API Documentation

## Overview

---

## Table of Contents

1. [Standard CRUD Operations](#standard-crud-operations)

---

## Standard CRUD Operations

### Create Book
```bash
    curl --location 'http://localhost:8080/api/books' \
    --header 'Accept-Version: application/vnd.sample-service.v1' \
    --header 'X-Author: pepito' \
    --header 'X-Action: CreateBook' \
    --header 'X-Justification: bla bla bla' \
    --header 'Content-Type: application/json' \
    --data '{
        "title": "Handmade Concrete Table",
        "isbn": "978-463-212-680-1",
        "author": "Yazmin_Kshlerin89",
        "published_year": 1981
    }'
```

### Get All Books
```bash
    curl --location 'http://localhost:8080/api/books' \
    --header 'Accept-Version: application/vnd.sample-service.v1'
```

### Get Book by ISBN
```bash
    curl --location 'http://localhost:8080/api/books/978-1-123456986' \
    --header 'Accept-Version: application/vnd.sample-service.v1'
```

### Update Book
```bash
    curl --location --request PUT 'http://localhost:8080/api/books/7' \
    --header 'Accept-Version: application/vnd.sample-service.v1' \
    --header 'X-Author: Pepe' \
    --header 'X-Justification: blu blu blu' \
    --header 'Content-Type: application/json' \
    --data '{
        "title": "Ergonomic Granite Hat",
        "isbn": "978-463-212-680-1",
        "author": "Melba.Carroll",
        "published_year": 1981
    }'
```

### Delete Book
```bash    
    curl --location --request DELETE 'http://localhost:8080/api/books/978-1-123456986' \
    --header 'Accept-Version: application/vnd.sample-service.v1'
```
---
