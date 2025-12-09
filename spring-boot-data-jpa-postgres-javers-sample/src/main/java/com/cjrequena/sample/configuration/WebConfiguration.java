package com.cjrequena.sample.configuration;

import com.cjrequena.sample.shared.common.audit.AuditHeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    
    private final AuditHeaderInterceptor auditHeaderInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditHeaderInterceptor)
                .addPathPatterns("/api/**");
    }
}
