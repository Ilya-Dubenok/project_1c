CREATE DATABASE category_service
WITH
    OWNER = root
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
;

\connect category_service;


CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS app.category
(
    category_id uuid NOT NULL,
    name character varying(255)  NOT NULL,
    parent_id uuid,
    CONSTRAINT category_pkey PRIMARY KEY (category_id),
    CONSTRAINT category_name_unique_constraint UNIQUE (name),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id)
        REFERENCES app.category (category_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


CREATE TABLE IF NOT EXISTS app.expiration_rule
(
    uuid uuid NOT NULL,
    days_till_expiration integer NOT NULL,
    rule_type character varying(255) NOT NULL,
    CONSTRAINT expiration_rule_pkey PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS app.quantity_rule
(
    uuid uuid NOT NULL,
    minimum_quantity integer NOT NULL,
    rule_type character varying(255) NOT NULL,
    CONSTRAINT quantity_rule_pkey PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS app.categories_rules
(
    category_id uuid NOT NULL,
    rule_type character varying(255) NOT NULL,
    rule_id uuid NOT NULL,
    CONSTRAINT fk_categories_rules_category FOREIGN KEY (category_id)
        REFERENCES app.category (category_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
