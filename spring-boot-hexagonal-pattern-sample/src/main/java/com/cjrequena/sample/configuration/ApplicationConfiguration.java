package com.cjrequena.sample.configuration;

import com.cjrequena.sample.adapter.in.api.rest.CustomerAPIAdapter;
import com.cjrequena.sample.adapter.out.persistence.CustomerJpaAdapter;
import com.cjrequena.sample.adapter.out.persistence.repository.CustomerJpaRepository;
import com.cjrequena.sample.application.service.CustomerService;
import com.cjrequena.sample.domain.port.in.CustomerAPIPort;
import com.cjrequena.sample.domain.port.out.CustomerJpaPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CustomerJpaPort customerJpaPort(CustomerJpaRepository repository) {
        return new CustomerJpaAdapter(repository);
    }

    @Bean
    public CustomerAPIPort customerAPIPort(CustomerService customerService) {
        return new CustomerAPIAdapter(customerService);
    }
}
