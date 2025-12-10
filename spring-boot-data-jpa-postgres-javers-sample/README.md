## Create a book
```bash
    curl --location 'http://localhost:8080/api/books' \
    --header 'Accept-Version: application/vnd.sample-service.v1' \
    --header 'X-Author: pepito' \
    --header 'X-Action: CreateBook' \
    --header 'X-Justification: bla bla bla' \
    --header 'Content-Type: application/json' \
    --data '{
        "title": "Unbranded Rubber Mouse",
        "isbn": "8272369109003",
        "author": "Alessia_Kiehn",
        "published_year": 1981
    }   '
```

## Retrieve book by id
```bash
    curl --location 'http://localhost:8080/api/books/1' \
    --header 'Accept-Version: application/vnd.sample-service.v1'
```

## Retrieve book list
```bash
    curl --location 'http://localhost:8080/api/books' \
    --header 'Accept-Version: application/vnd.sample-service.v1'
```

## Update book
```bash
    curl --location --request PUT 'http://localhost:8080/api/books/1' \
    --header 'Accept-Version: application/vnd.sample-service.v1' \
    --header 'X-Author: Pepe' \
    --header 'X-Justification: blu blu blu' \
    --header 'Content-Type: application/json' \
    --data '{
        "title": "Practical Steel Salad",
        "isbn": "8272369109003",
        "author": "Charley_Steuber43",
        "published_year": 1981
    }'
```

## Audit changes
```bash
    curl --location 'http://localhost:8080/api/books/1/audit-changes' \
    --header 'Accept-Version: application/vnd.sample-service.v1' \
    --header 'accept: application/json'
```

## Audit history
```bash
    curl --location 'http://localhost:8080/api/books/1/audit-history' \
    --header 'Accept-Version: application/vnd.sample-service.v1' \
    --header 'accept: application/json'
```
