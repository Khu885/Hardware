package com.hardwareaplications.hardware.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;

@Configuration
public class ValidatorConfig {

    /**
     * Expose a Spring-aware ValidatorFactory so that custom ConstraintValidator
     * implementations (like ExistsProductValidator) get their @Autowired
     * dependencies injected properly.
     */
    @Bean
    @Primary
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    /**
     * Tell Hibernate to use the Spring-managed ValidatorFactory instead of
     * creating its own. This ensures @Autowired works inside ConstraintValidator classes.
     */
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            LocalValidatorFactoryBean validatorFactory) {
        return hibernateProperties -> {
            hibernateProperties.put("jakarta.persistence.validation.factory", validatorFactory);
            hibernateProperties.put("javax.persistence.validation.factory", validatorFactory);
        };
    }
}

