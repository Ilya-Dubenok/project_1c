package org.example.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = ExpirationRule.class, name = "exp"),
        @JsonSubTypes.Type(value = QuantityRule.class, name = "q")})
public interface IRule {

    RuleType getRuleType();

    void apply();

}
