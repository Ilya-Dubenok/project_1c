package org.example.dao.entities.product;


public interface IRule {

    RuleType getRuleType();

    void apply();

}
