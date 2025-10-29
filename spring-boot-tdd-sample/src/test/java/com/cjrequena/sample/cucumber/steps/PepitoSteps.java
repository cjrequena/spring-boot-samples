package com.cjrequena.sample.cucumber.steps;

import com.cjrequena.sample.persistence.entity.CustomerEntity;
import com.cjrequena.sample.persistence.jpa.repository.CustomerRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class PepitoSteps {

  @Autowired
  TestRestTemplate restTemplate;
  @Autowired
  private CustomerRepository customerRepository;

  private String response;
  private CustomerEntity testCustomer;

  @Given("A customer exists with the following details:")
  public void a_customer_exists_with_the_following_details(io.cucumber.datatable.DataTable dataTable) {
    Map<String, String> customerData = dataTable.asMaps().get(0);

    testCustomer = CustomerEntity.builder()
      .firstName(customerData.get("firstName"))
      .lastName(customerData.get("lastName"))
      .email(customerData.get("email"))
      .phoneNumber("1234567890")
      .build();

    testCustomer = customerRepository.save(testCustomer);
  }

  @Given("the app is running")
  public void the_app_is_running() {
    // optional health check
  }

  @When("I say hello")
  public void i_say_hello() {
    response = restTemplate.getForObject("/hello", String.class);
  }

  @Then("I should get {string}")
  public void i_should_get(String expected) {
    assertThat(response).isEqualTo(expected);
  }
}
