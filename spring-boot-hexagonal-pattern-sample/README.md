# spring-boot-hexagonal-pattern-sample

![img.png](img.png)

![img_1.png](img_1.png)

## Rest Interface

### Create
```bash

```
### RetrieveById
```bash

```
### RetrieveAll
```bash

```

## Grpc interface

### Create

```bash
    grpcurl --plaintext \
      -d '{"customer": {"name": "Giga Chad", "email": "email@grpc.com"}}' \
      localhost:18080 com.cjrequena.sample.grpc.service.CustomerService/CreateCustomer
```

### RetirveById

```bash
    grpcurl --plaintext \
      -d '{"id": 1}' \
      localhost:18080 com.cjrequena.sample.grpc.service.CustomerService/RetrieveCustomerById
```

### RetrieveAll
```bash
    grpcurl --plaintext \
      localhost:18080 com.cjrequena.sample.grpc.service.CustomerService/RetrieveCustomers
```
