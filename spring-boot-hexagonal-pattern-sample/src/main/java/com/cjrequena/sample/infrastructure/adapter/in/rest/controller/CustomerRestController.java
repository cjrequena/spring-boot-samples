package com.cjrequena.sample.infrastructure.adapter.in.rest.controller;

import com.cjrequena.sample.application.service.CustomerService;
import com.cjrequena.sample.domain.exception.domain.CustomerNotFoundException;
import com.cjrequena.sample.domain.exception.rest.NotFoundException;
import com.cjrequena.sample.domain.mapper.CustomerMapper;
import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.domain.port.in.customer.CreateCustomerUseCase;
import com.cjrequena.sample.infrastructure.adapter.in.rest.dto.CustomerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerRestController {

  private final CustomerService customerService;
  private final CreateCustomerUseCase createCustomerUseCase;
  private final CustomerMapper customerMapper;

  @PostMapping(
    path = "",
    produces = {APPLICATION_JSON_VALUE}
  )
  public CustomerDTO create(@RequestBody CustomerDTO customerDTO) {
    Customer customer = customerMapper.toAggregate(customerDTO);
    customer = createCustomerUseCase.create(customer);
    return this.customerMapper.toDTO(customer);
  }

  @GetMapping(
    path = "",
    produces = {APPLICATION_JSON_VALUE}
  )  public ResponseEntity<List<CustomerDTO>> retrieve() {
    final List<Customer> customerList = this.customerService.retrieve();
    final List<CustomerDTO> customerDTOList = this.customerMapper.toDTOList(customerList);
    HttpHeaders responseHeaders = new HttpHeaders();
    return new ResponseEntity<>(customerDTOList, responseHeaders, HttpStatus.OK);
  }

  @GetMapping(
    path = "/{customerId}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<CustomerDTO> retrieveById(@PathVariable("customerId")  Long id) {
    try {
      final CustomerDTO customerDTO = this.customerMapper.toDTO(this.customerService.retrieveById(id));
      HttpHeaders responseHeaders = new HttpHeaders();
      return new ResponseEntity<>(customerDTO, responseHeaders, HttpStatus.OK);
    } catch (CustomerNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }
}
