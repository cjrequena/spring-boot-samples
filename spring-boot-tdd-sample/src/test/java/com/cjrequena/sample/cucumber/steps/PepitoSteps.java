package com.cjrequena.sample.cucumber.steps;

import com.cjrequena.sample.controller.dto.OrderDTO;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.persistence.entity.CustomerEntity;
import com.cjrequena.sample.persistence.jpa.repository.CustomerRepository;
import com.cjrequena.sample.persistence.jpa.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@CucumberContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PepitoSteps {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private CustomerEntity testCustomer;
  private OrderDTO currentOrderDTO;
  private ResultActions lastResult;
  private OrderDTO createdOrder;
  private List<OrderDTO> retrievedOrders;

  @Given("A customer exists with the following details:")
  public void a_customer_exists_with_the_following_details(io.cucumber.datatable.DataTable dataTable) {
    Map<String, String> customerData = dataTable.asMaps().get(0);
    testCustomer = CustomerEntity
      .builder()
      .firstName(customerData.get("firstName"))
      .lastName(customerData.get("lastName"))
      .email(customerData.get("email"))
      .phoneNumber("1234567890")
      .build();
    testCustomer = customerRepository.save(testCustomer);
  }

  @Given("I have order details with status {string}")
  public void i_have_order_details_with_status(String status) {
    currentOrderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.valueOf(status))
      .totalAmount(BigDecimal.valueOf(100.00))
      .customerId(testCustomer.getId())
      .build();
  }

  @When("I create a new order")
  public void i_create_a_new_order() throws Exception {
    lastResult = mockMvc.perform(post("/api/orders")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(currentOrderDTO)));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 201) {
      createdOrder = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO.class);
    }
  }

  @Then("the order should be created successfully")
  public void the_order_should_be_created_successfully() throws Exception {
    lastResult.andExpect(status().isCreated());
    assertThat(createdOrder).isNotNull();
    assertThat(createdOrder.getId()).isNotNull();
  }

  @Then("the order should have status {string}")
  public void the_order_should_have_status(String status) {
    assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.valueOf(status));
  }

  @Then("the order should have a valid order number")
  public void the_order_should_have_a_valid_order_number() {
    assertThat(createdOrder.getOrderNumber()).isNotNull();
    assertThat(createdOrder.getOrderNumber()).matches("^ORD-\\d{8}-\\d{5}$");
  }
}
