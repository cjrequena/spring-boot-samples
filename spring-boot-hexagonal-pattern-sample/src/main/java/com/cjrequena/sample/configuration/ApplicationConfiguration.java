package com.cjrequena.sample.configuration;

import com.cjrequena.sample.adapter.in.api.rest.CustomerRestAdapter;
import com.cjrequena.sample.adapter.out.persistence.CustomerJpaAdapter;
import com.cjrequena.sample.adapter.out.persistence.repository.CustomerJpaRepository;
import com.cjrequena.sample.application.service.CustomerService;
import com.cjrequena.sample.domain.port.in.api.CustomerRestPort;
import com.cjrequena.sample.domain.port.out.persistence.CustomerJpaPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CustomerJpaPort customerJpaPort(CustomerJpaRepository repository) {
        return new CustomerJpaAdapter(repository);
    }

    @Bean
    public CustomerRestPort customerRestPort(CustomerService customerService) {
        return new CustomerRestAdapter(customerService);
    }
}
