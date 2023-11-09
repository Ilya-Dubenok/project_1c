CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS app.category
(
    id uuid NOT NULL,
    name character varying(255)  NOT NULL,
    parent_id uuid,
    CONSTRAINT category_pkey PRIMARY KEY (id),
    CONSTRAINT category_name_unique_constraint UNIQUE (name),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id)
        REFERENCES app.category (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


CREATE TABLE IF NOT EXISTS app.expiration_rule
(
    id uuid NOT NULL,
    days_till_expiration integer NOT NULL,
    rule_type smallint,
    CONSTRAINT expiration_rule_pkey PRIMARY KEY (id),
    CONSTRAINT expiration_rule_rule_type_check CHECK (rule_type >= 0 AND rule_type <= 1)
);

CREATE TABLE IF NOT EXISTS app.quantity_rule
(
    id uuid NOT NULL,
    minimum_quantity integer NOT NULL,
    rule_type smallint,
    CONSTRAINT quantity_rule_pkey PRIMARY KEY (id),
    CONSTRAINT quantity_rule_rule_type_check CHECK (rule_type >= 0 AND rule_type <= 1)
);

CREATE TABLE IF NOT EXISTS app.categories_rules
(
    category_id uuid NOT NULL,
    rule_type character varying(255) NOT NULL,
    rule_id uuid NOT NULL,
    CONSTRAINT fk_categories_rules_category FOREIGN KEY (category_id)
        REFERENCES app.category (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

