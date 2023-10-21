package org.example.dao.entities;

import jakarta.persistence.*;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.ExpirationRule;
import org.example.entities.IRule;
import org.example.entities.QuantityRule;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "category",
        uniqueConstraints = {@UniqueConstraint(name = "category_name_unique_constraint", columnNames = {"name"})})
public class Category {

    @Id
    @Column(name = "category_id")
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "category_id", foreignKey = @ForeignKey(name = "fk_category_parent"))
    private Category parent;

    @ManyToAny
    @Fetch(FetchMode.JOIN)
    @AnyDiscriminator(DiscriminatorType.STRING)
    @Column(name = "rule_type")
    @AnyKeyJavaClass(UUID.class)
    @AnyDiscriminatorValue(discriminator = "E", entity = ExpirationRule.class)
    @AnyDiscriminatorValue(discriminator = "Q", entity = QuantityRule.class)
    @Cascade(CascadeType.ALL)
    @JoinTable(name = "categories_rules",
            joinColumns = @JoinColumn(name = "category_id"),
            foreignKey = @ForeignKey(name = "fk_categories_rules_category"),
            inverseJoinColumns = @JoinColumn(name = "rule_id"),
            inverseForeignKey = @ForeignKey(name = "fk_categories_rules_rule"))
    private List<IRule> rules;

    public Category(String name) {
        this.name = name;
    }

    public Category(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
