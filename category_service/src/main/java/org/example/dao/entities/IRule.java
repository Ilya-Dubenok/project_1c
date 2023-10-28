package org.example.dao.entities;


public interface IRule {

    RuleType getRuleType();

    void apply();

}
