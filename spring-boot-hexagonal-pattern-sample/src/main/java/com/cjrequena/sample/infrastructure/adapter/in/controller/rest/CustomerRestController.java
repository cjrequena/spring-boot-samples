package com.cjrequena.sample.infrastructure.adapter.in.controller.rest;

import com.cjrequena.sample.application.service.CustomerService;
import com.cjrequena.sample.domain.exception.controller.NotFoundException;
import com.cjrequena.sample.domain.exception.domain.CustomerNotFoundException;
import com.cjrequena.sample.domain.mapper.CustomerMapper;
import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.domain.port.in.customer.CreateCustomerUseCase;
import com.cjrequena.sample.domain.port.in.customer.RetrieveCustomerUseCase;
import com.cjrequena.sample.infrastructure.adapter.in.controller.dto.CustomerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cjrequena.sample.shared.common.util.Constant.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = CustomerRestController.ENDPOINT, headers = {CustomerRestController.ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerRestController {

  public static final String ENDPOINT = "/foo-service/api/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  private final CustomerService customerService;
  private final CreateCustomerUseCase createCustomerUseCase;
  private final RetrieveCustomerUseCase retrieveCustomerUseCase;
  private final CustomerMapper customerMapper;

  @PostMapping(
    path = "/customers",
    produces = {APPLICATION_JSON_VALUE}
  )
  public CustomerDTO create(@RequestBody CustomerDTO customerDTO) {
    Customer customer = customerMapper.toAggregate(customerDTO);
    customer = createCustomerUseCase.create(customer);
    return this.customerMapper.toDTO(customer);
  }

  @GetMapping(
    path = "/customers",
    produces = {APPLICATION_JSON_VALUE}
  )  public ResponseEntity<List<CustomerDTO>> retrieve() {
    final List<Customer> customerList = this.retrieveCustomerUseCase.retrieve();
    final List<CustomerDTO> customerDTOList = this.customerMapper.toDTOList(customerList);
    HttpHeaders responseHeaders = new HttpHeaders();
    return new ResponseEntity<>(customerDTOList, responseHeaders, HttpStatus.OK);
  }

  @GetMapping(
    path = "/customers/{customerId}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<CustomerDTO> retrieveById(@PathVariable("customerId")  Long id) {
    try {
      final CustomerDTO customerDTO = this.customerMapper.toDTO(this.retrieveCustomerUseCase.retrieveById(id));
      HttpHeaders responseHeaders = new HttpHeaders();
      return new ResponseEntity<>(customerDTO, responseHeaders, HttpStatus.OK);
    } catch (CustomerNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }
}
