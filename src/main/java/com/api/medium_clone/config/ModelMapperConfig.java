package com.api.medium_clone.config;

import org.hibernate.collection.spi.PersistentSet;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addConverter((MappingContext<Object, Object> context) -> {
            if (context.getSource() instanceof PersistentSet) {
                return !((PersistentSet<?>) context.getSource()).isEmpty();
            }
            return context.getSource();
        });

        return modelMapper;
    }
}
