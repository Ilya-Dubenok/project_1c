package org.example.config;


import org.example.utils.exception.ConstraintMapper;
import org.example.utils.exception.DataBaseExceptionParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataBaseExceptionsConfig {

    @Bean
    public DataBaseExceptionParser dataBaseExceptionsHandler() {

        DataBaseExceptionParser dataBaseExceptionParser = DataBaseExceptionParser.Builder.builder()
                .addConstraint(new ConstraintMapper("category_name_unique_constraint", "this name already exists"))
                .addConstraint(new ConstraintMapper("fk_category_parent", "he has children!"))
                .build();

        return dataBaseExceptionParser;

    }


}
