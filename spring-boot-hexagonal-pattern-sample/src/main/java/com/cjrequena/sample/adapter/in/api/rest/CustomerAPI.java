package com.cjrequena.sample.adapter.in.api.rest;

import com.cjrequena.sample.domain.model.Customer;
import com.cjrequena.sample.domain.port.in.CustomerServicePort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerAPI  {

    private final CustomerServicePort customerServicePort;

    public CustomerAPI(CustomerServicePort customerServicePort) {
        this.customerServicePort = customerServicePort;
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return customerServicePort.createCustomer(customer);
    }
}
