package com.cjrequena.sample.configuration;

import com.cjrequena.sample.adapter.out.persistence.CustomerPersistenceAdapter;
import com.cjrequena.sample.adapter.out.persistence.repository.CustomerJpaRepository;
import com.cjrequena.sample.application.service.CustomerService;
import com.cjrequena.sample.domain.port.in.CustomerServicePort;
import com.cjrequena.sample.domain.port.out.CustomerRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CustomerRepositoryPort customerRepositoryPort(CustomerJpaRepository repository) {
        return new CustomerPersistenceAdapter(repository);
    }

    @Bean
    public CustomerServicePort customerServicePort(CustomerRepositoryPort customerRepositoryPort) {
        return new CustomerService(customerRepositoryPort);
    }
}
